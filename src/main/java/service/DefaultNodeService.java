package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import repository.INodeRepository;

import javax.annotation.Nonnull;

@Service
public class DefaultNodeService implements INodeService {
    private final INodeRepository repository;

    @Autowired
    public DefaultNodeService(@Qualifier("neo4jNodeRepository") @Nonnull INodeRepository repository) {
        this.repository = repository;
    }
}
