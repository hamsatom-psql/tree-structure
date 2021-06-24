package repository;

import model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository("neo4jNodeRepository")
public class Neo4jNodeRepository implements INodeRepository {
    private final DataSource neo4j;

    @Autowired
    public Neo4jNodeRepository(DataSource neo4j) throws SQLException {
        this.neo4j = neo4j;
        initSchema();
    }

    private void initSchema() throws SQLException {
        try (
                Connection connection = neo4j.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("CREATE CONSTRAINT ON (n:Node) ASSERT n.id IS UNIQUE")
        ) {
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void saveNode(@Nonnull String nodeId, @Nullable String parentId) throws SQLException {
        Connection connection = null;
        try {
            connection = neo4j.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement("CREATE (n:Node {id: ?})")) {
                preparedStatement.setString(1, nodeId);
                preparedStatement.executeUpdate();
            }
            if (parentId != null) {
                attach(nodeId, parentId, connection);
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Node toNode(ResultSet resultSet) throws SQLException {
        return new Node()
                .setId(resultSet.getString("id"))
                .setDepth(resultSet.getInt("depth"))
                .setRootId(resultSet.getString("rootId"));
    }

    @Override
    public boolean exists(@Nonnull String id) throws SQLException {
        try (
                Connection connection = neo4j.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "MATCH (n:Node {id: ?}) RETURN n"
                )
        ) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    @Nonnull
    @Override
    public List<Node> selectDescendants(@Nonnull String parentId) throws SQLException {
        try (
                Connection connection = neo4j.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "MATCH (root:Node)-[parents*0..]->(parent:Node {id: ?})-[:PARENT_OF]->(descendant:Node) " +
                                "WHERE NOT ()-[:PARENT_OF]->(root) " +
                                "RETURN root.id AS rootId, LENGTH(parents) + 1 AS depth, descendant.id as id"
                )
        ) {
            preparedStatement.setString(1, parentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Node> descendants = new ArrayList<>();
            while (resultSet.next()) {
                descendants.add(toNode(resultSet).setParentId(parentId));
            }
            return descendants;
        }
    }

    @Override
    public void updateNodeParent(@Nonnull String nodeId, @Nonnull String newParentId) throws SQLException {
        Connection connection = null;
        try {
            connection = neo4j.getConnection();
            connection.setAutoCommit(false);

            detach(nodeId, connection);
            attach(nodeId, newParentId, connection);
            try (PreparedStatement statement = connection.prepareStatement(
                    "MATCH p=(n:Node {id: ?})-[:PARENT_OF*]->(n) RETURN p AS cycle"
            )) {
                statement.setString(1, nodeId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    throw new IllegalArgumentException("Attaching " + nodeId + " to " + newParentId + " creates cycle");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void detachFromParent(@Nonnull String nodeId) throws SQLException {
        try (Connection connection = neo4j.getConnection()) {
            detach(nodeId, connection);
        }
    }

    private void detach(@Nonnull String nodeId, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("MATCH (:Node)-[r:PARENT_OF]->(:Node {id: ?}) DELETE r")) {
            preparedStatement.setString(1, nodeId);
            preparedStatement.executeUpdate();
        }
    }

    private void attach(String nodeId, String parentId, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "MATCH (child:Node), (parent:Node) WHERE child.id = ? AND parent.id = ? CREATE (parent)-[:PARENT_OF]->(child)"
        )) {
            statement.setString(1, nodeId);
            statement.setString(2, parentId);
            statement.executeUpdate();
        }
    }
}
