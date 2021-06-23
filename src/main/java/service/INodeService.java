package service;

import model.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface INodeService {
    void createNode(@Nonnull String id);

    @Nonnull
    List<Node> getDescendants(@Nonnull String parentId);

    void updateNodeParent(@Nonnull String nodeId, @Nonnull String newParentId);

    default void createNodeUnderParent(@Nonnull String nodeId, @Nullable String parentId) {
        createNode(nodeId);
        if (parentId != null) {
            updateNodeParent(nodeId, parentId);
        }
    }
}
