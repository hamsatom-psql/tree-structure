package repository;

import model.Node;

import javax.annotation.Nonnull;
import java.util.List;

public interface INodeRepository {
    void saveNode(@Nonnull String id);

    @Nonnull
    List<Node> selectDescendants(@Nonnull String parentId);

    void updateNodeParent(@Nonnull String nodeId, @Nonnull String newParentId);
}
