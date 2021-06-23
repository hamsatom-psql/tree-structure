package repository;

import model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.util.List;

@Repository("neo4jNodeRepository")
public class Neo4jNodeRepository implements INodeRepository {
    private final DataSource neo4j;

    @Autowired
    public Neo4jNodeRepository(DataSource neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public void saveNode(@Nonnull String nodeId, @Nullable String parentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nonnull
    @Override
    public List<Node> selectDescendants(@Nonnull String parentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void updateNodeParent(@Nonnull String nodeId, @Nonnull String newParentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void detachFromParent(@Nonnull String nodeId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
