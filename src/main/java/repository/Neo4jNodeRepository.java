package repository;

import model.Node;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository("neo4jNodeRepository")
public class Neo4jNodeRepository implements INodeRepository {
    @Override
    public void saveNode(@Nonnull String id) {
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
}
