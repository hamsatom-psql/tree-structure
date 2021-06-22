package controller;

import model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.INodeService;

import javax.annotation.Nonnull;
import java.util.List;

@RestController("/node")
public class RestNodeController {
    private final INodeService service;

    @Autowired
    public RestNodeController(@Nonnull INodeService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Nonnull
    public ResponseEntity<Object> createNode(@RequestBody @Nonnull Node node) {

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{parentId}/descendants", produces = MediaType.APPLICATION_JSON_VALUE)
    @Nonnull
    public List<Node> getDescendants(@PathVariable @Nonnull String parentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @PutMapping(path = "/{id}/parent/{parentId}")
    @Nonnull
    public ResponseEntity<Object> changeParent(@PathVariable @Nonnull String id, @PathVariable @Nonnull String parentId) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
