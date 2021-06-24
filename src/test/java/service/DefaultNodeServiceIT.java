package service;

import model.DuplicateIdException;
import model.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AbstractNodeIT;
import repository.Neo4jNodeRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

class DefaultNodeServiceIT extends AbstractNodeIT {
    private DefaultNodeService service;

    @BeforeEach
    void setUp() throws SQLException {
        service = new DefaultNodeService(new Neo4jNodeRepository(neo4j));
    }

    @Test
    void createNode() {
        service.createNode("a", null);
        service.createNode("b", "a");
        List<Node> descendants = service.getDescendants("a");
        Assertions.assertEquals(1, descendants.size());
        Assertions.assertEquals("b", descendants.get(0).getId());
        nodeEquals(descendants.get(0), "a", "a", 1);
    }

    @Test
    void createNode_invalidParent() {
        Assertions.assertThrows(NoSuchElementException.class, () -> service.createNode("b", "a"));
    }

    @Test
    void createNode_cycle() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.createNode("b", "b"));
    }

    @Test
    void createNode_duplicate() {
        service.createNode("b", null);
        Assertions.assertThrows(DuplicateIdException.class, () -> service.createNode("b", null));
    }

    @Test
    void getDescendants_root() {
        service.createNode("root", null);
        List<Node> descendants = service.getDescendants("root");
        Assertions.assertTrue(descendants.isEmpty());
    }

    @Test
    void getDescendants_missing() {
        Assertions.assertThrows(NoSuchElementException.class, () -> service.getDescendants("b"));
    }

    @Test
    void updateNodeParent() {
        service.createNode("b", null);
        service.createNode("c", null);
        service.updateNodeParent("c", "b");
        List<Node> descendants = service.getDescendants("b");
        Assertions.assertEquals(1, descendants.size());
        Assertions.assertEquals("c", descendants.get(0).getId());
        nodeEquals(descendants.get(0), "b", "b", 1);
    }

    @Test
    void updateNodeParent_toRoot() {
        service.createNode("b", null);
        service.createNode("c", "b");
        service.updateNodeParent("c", null);
        List<Node> descendants = service.getDescendants("c");
        Assertions.assertTrue(descendants.isEmpty());
    }

    @Test
    void updateNodeParent_cycle() {
        service.createNode("b", null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateNodeParent("b", "b"));
    }

    @Test
    void updateNodeParent_missingNode() {
        service.createNode("b", null);
        Assertions.assertThrows(NoSuchElementException.class, () -> service.updateNodeParent("c", "b"));
    }

    @Test
    void updateNodeParent_missingParent() {
        service.createNode("a", null);
        Assertions.assertThrows(NoSuchElementException.class, () -> service.updateNodeParent("a", "b"));
    }
}