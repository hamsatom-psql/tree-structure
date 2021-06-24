package repository;

import model.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.exceptions.ClientException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

class Neo4jNodeRepositoryIT extends AbstractNodeIT {
    private Neo4jNodeRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new Neo4jNodeRepository(neo4j);
    }

    private void testTree(String root, String parent, int childDepth, String... leaves) throws SQLException {
        for (String leaf : leaves) {
            repository.saveNode(leaf, parent);
        }
        testDescendants(root, parent, childDepth, leaves);
    }

    private void testDescendants(String root, String parent, int childDepth, String... leaves) throws SQLException {
        List<Node> descendants = repository.selectDescendants(parent);
        Assertions.assertEquals(leaves.length, descendants.size());
        for (String leaf : leaves) {
            Optional<Node> descendant = descendants.stream().filter(node -> leaf.equals(node.getId())).findAny();
            Assertions.assertTrue(descendant.isPresent());
            nodeEquals(descendant.get(), parent, root, childDepth);
        }
    }

    @Test
    void saveNode_duplicate() throws SQLException {
        repository.saveNode("root", null);
        Assertions.assertThrows(ClientException.class, () -> repository.saveNode("root", null));
    }

    @Test
    void saveNode_root() throws SQLException {
        repository.saveNode("root", null);
        testTree("root", "root", 0);
    }

    @Test
    void saveNode_1level() throws SQLException {
        repository.saveNode("root", null);
        testTree("root", "root", 1, "descendant1", "descendant2");
    }

    @Test
    void saveNode_2levels() throws SQLException {
        repository.saveNode("root", null);
        testTree("root", "root", 1, "parent1", "parent2");
        testTree("root", "parent1", 2, "child1", "child2");
        testTree("root", "parent2", 2, "child3", "child4");
        List<Node> descendants = repository.selectDescendants("root");
        descendants.forEach(descendant -> Assertions.assertEquals(1, descendant.getDepth()));
    }

    @Test
    void saveNode_forest() throws SQLException {
        repository.saveNode("root1", null);
        repository.saveNode("root2", null);
        repository.saveNode("child1", "root1");
        repository.saveNode("child2", "root2");
        testDescendants("root1", "root1", 1, "child1");
        testDescendants("root2", "root2", 1, "child2");
    }

    @Test
    void updateNodeParent_newRoot() throws SQLException {
        repository.saveNode("root", null);
        repository.saveNode("mid1", "root");
        repository.saveNode("mid2", "root");
        repository.saveNode("leaf1", "mid1");
        repository.saveNode("leaf2", "mid1");
        repository.saveNode("root2", null);
        repository.updateNodeParent("root", "root2");
        testDescendants("root2", "root2", 1, "root");
        testDescendants("root2", "root", 2, "mid1", "mid2");
        testDescendants("root2", "mid1", 3, "leaf1", "leaf2");
    }

    @Test
    void updateNodeParent_newInnerNode() throws SQLException {
        repository.saveNode("1", null);
        repository.saveNode("2", "1");
        repository.saveNode("3", "2");
        repository.saveNode("21", null);
        repository.updateNodeParent("21", "1");
        repository.updateNodeParent("2", "21");
        testDescendants("1", "1", 1, "21");
        testDescendants("1", "21", 2, "2");
        testDescendants("1", "2", 3, "3");
    }

    @Test
    void updateNodeParent_newLeaf() throws SQLException {
        repository.saveNode("1", null);
        repository.saveNode("2", "1");
        repository.saveNode("3", "2");
        repository.saveNode("4", null);
        repository.updateNodeParent("4", "3");
        testDescendants("1", "1", 1, "2");
        testDescendants("1", "2", 2, "3");
        testDescendants("1", "3", 3, "4");
    }

    @Test
    void updateNodeParent_cycle() throws SQLException {
        repository.saveNode("1", null);
        repository.saveNode("2", "1");
        repository.saveNode("3", "2");
        Assertions.assertThrows(RuntimeException.class, () -> repository.updateNodeParent("2", "3"));
        testDescendants("1", "2", 2, "3");
    }

    @Test
    void detachFromParent() throws SQLException {
        repository.saveNode("1", null);
        repository.saveNode("2", "1");
        repository.detachFromParent("2");
        testDescendants("1", "1", 0);
        Assertions.assertTrue(repository.exists("2"));
    }

    @Test
    void detachFromParent_existingToRoot() throws SQLException {
        repository.saveNode("1", null);
        repository.saveNode("2", "1");
        repository.saveNode("3", "2");
        repository.detachFromParent("3");
        repository.updateNodeParent("1", "3");
        testDescendants("3", "3", 1, "1");
        testDescendants("3", "1", 2, "2");
    }

    @Test
    void exists() throws SQLException {
        repository.saveNode("node", null);
        Assertions.assertTrue(repository.exists("node"));
    }

    @Test
    void exists_missing() throws SQLException {
        Assertions.assertFalse(repository.exists("gone"));
    }
}