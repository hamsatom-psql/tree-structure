package service;

import model.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface INodeService {
    void createNode(@Nonnull String id, @Nullable String parentId);

    @Nonnull
    List<Node> getDescendants(@Nonnull String parentId);

    void updateNodeParent(@Nonnull String nodeId, @Nullable String newParentId);
}
