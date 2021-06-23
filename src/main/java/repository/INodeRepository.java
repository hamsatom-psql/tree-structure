package repository;

import model.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface INodeRepository {
    void saveNode(@Nonnull String nodeId, @Nullable String parentId);

    @Nonnull
    List<Node> selectDescendants(@Nonnull String parentId);

    void updateNodeParent(@Nonnull String nodeId, @Nonnull String newParentId);

    void detachFromParent(@Nonnull String nodeId);
}
