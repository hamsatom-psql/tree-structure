package repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import model.Node;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractNodeIT {
    protected static DataSource neo4j;

    @BeforeAll
    static void beforeAll() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:neo4j:bolt://localhost:7777/?user=neo4j,password=test,scheme=basic");
        neo4j = new HikariDataSource(config);
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (
                Connection connection = neo4j.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("MATCH (n) DETACH DELETE n")
        ) {
            preparedStatement.executeUpdate();
        }
    }

    public void nodeEquals(Node node, String parentId, String rootId, int depth) {
        Assertions.assertEquals(parentId, node.getParentId());
        Assertions.assertEquals(rootId, node.getRootId());
        Assertions.assertEquals(depth, node.getDepth());
    }
}
