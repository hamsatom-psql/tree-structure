package service;

import model.DuplicateIdException;
import model.Node;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import repository.INodeRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
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
        try {
            repository.saveNode(nodeId, parentId);
        } catch (ClientException e) {
            throw new DuplicateIdException("Duplicate id " + nodeId, e);
        } catch (SQLException throwables) {
            throw new IllegalStateException("Generic database error", throwables);
        }
    }

    @Nonnull
    @Override
    public List<Node> getDescendants(@Nonnull String parentId) {
        // TODO FIXME maybe check that parentId is persisted
        try {
            return repository.selectDescendants(parentId);
        } catch (SQLException throwables) {
            throw new IllegalStateException("Generic database error", throwables);
        }
    }

    @Override
    public void updateNodeParent(@Nonnull String nodeId, @Nullable String newParentId) {
        if (nodeId.equals(newParentId)) {
            throw new IllegalArgumentException("Same node and parent id " + nodeId);
        }
        // TODO FIXME maybe check that both nodes exist
        try {
            if (newParentId == null) {
                repository.detachFromParent(nodeId);
            } else {
                repository.updateNodeParent(nodeId, newParentId);
            }
        } catch (SQLException throwables) {
            throw new IllegalStateException("Generic database error", throwables);
        }
    }
}
