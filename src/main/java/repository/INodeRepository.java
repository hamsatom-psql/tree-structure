package repository;

import model.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;

public interface INodeRepository {
    void saveNode(@Nonnull String nodeId, @Nullable String parentId) throws SQLException;

    boolean exists(@Nonnull String id) throws SQLException;

    @Nonnull
    List<Node> selectDescendants(@Nonnull String parentId) throws SQLException;

    void updateNodeParent(@Nonnull String nodeId, @Nonnull String newParentId) throws SQLException;

    void detachFromParent(@Nonnull String nodeId) throws SQLException;
}
