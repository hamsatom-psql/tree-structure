package service;

import model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import repository.INodeRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Service
public class DefaultNodeService implements INodeService {
    private final INodeRepository repository;

    @Autowired
    public DefaultNodeService(@Qualifier("neo4jNodeRepository") @Nonnull INodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createNode(@Nonnull String nodeId, @Nullable String parentId) {
        if (nodeId.equals(parentId)) {
            throw new IllegalArgumentException("Same node and parent id " + nodeId);
        }
        // TODO FIXME maybe check that id not already taken
        repository.saveNode(nodeId, parentId);
    }

    @Nonnull
    @Override
    public List<Node> getDescendants(@Nonnull String parentId) {
        // TODO FIXME maybe check that parentId is persisted
        return repository.selectDescendants(parentId);
    }

    @Override
    public void updateNodeParent(@Nonnull String nodeId, @Nullable String newParentId) {
        if (nodeId.equals(newParentId)) {
            throw new IllegalArgumentException("Same node and parent id " + nodeId);
        }
        // TODO FIXME maybe check that both nodes exist
        repository.updateNodeParent(nodeId, newParentId);
    }
}
