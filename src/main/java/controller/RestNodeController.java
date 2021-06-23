package controller;

import model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.INodeService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@RestController("/node")
public class RestNodeController {
    private final INodeService service;

    @Autowired
    public RestNodeController(@Nonnull INodeService service) {
        this.service = service;
    }

    @PostMapping(path = "/{id}")
    @Nonnull
    public ResponseEntity<Object> createNode(@PathVariable @Nonnull String id, @RequestParam(required = false) @Nullable String parentId) {
        service.createNodeUnderParent(id, parentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{parentId}/descendants", produces = MediaType.APPLICATION_JSON_VALUE)
    @Nonnull
    public List<Node> getDescendants(@PathVariable @Nonnull String parentId) {
        return service.getDescendants(parentId);
    }

    @PutMapping(path = "/{id}/parent/{parentId}")
    @Nonnull
    public ResponseEntity<Object> changeParent(@PathVariable @Nonnull String id, @PathVariable @Nonnull String parentId) {
        service.updateNodeParent(id, parentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
