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
import java.util.NoSuchElementException;

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
            if (parentId != null && !repository.exists(parentId)) {
                throw new NoSuchElementException("No node with id " + parentId);
            }
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
        try {
            if (!repository.exists(parentId)) {
                throw new NoSuchElementException("No node with id " + parentId);
            }
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
        try {
            if (!repository.exists(nodeId)) {
                throw new NoSuchElementException("No node with id " + nodeId);
            }
            if (newParentId == null) {
                repository.detachFromParent(nodeId);
            } else {
                if (!repository.exists(newParentId)) {
                    throw new NoSuchElementException("No node with id " + newParentId);
                }
                repository.updateNodeParent(nodeId, newParentId);
            }
        } catch (SQLException throwables) {
            throw new IllegalStateException("Generic database error", throwables);
        }
    }
}
