package controller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@RestController
public class RestNodeController {
    private final INodeService service;

    @Autowired
    public RestNodeController(@Nonnull INodeService service) {
        this.service = service;
    }

    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created node"),
            @ApiResponse(code = 400, message = "Invalid ids"),
            @ApiResponse(code = 404, message = "No node with parentId"),
            @ApiResponse(code = 409, message = "Duplicate nodeId"),
    })
    @PostMapping(path = "/node/{id}")
    @Nonnull
    public ResponseEntity<Object> createNode(@PathVariable @Nonnull String id, @RequestParam(required = false) @Nullable String parentId) {
        service.createNode(id, parentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved descendants of parentId"),
            @ApiResponse(code = 404, message = "No node with parentId"),
    })
    @GetMapping(path = "/node/{parentId}/descendants", produces = MediaType.APPLICATION_JSON_VALUE)
    @Nonnull
    public List<Node> getDescendants(@PathVariable @Nonnull String parentId) {
        return service.getDescendants(parentId);
    }

    @ApiResponses({
            @ApiResponse(code = 204, message = "Successfully changed node's parentId"),
            @ApiResponse(code = 400, message = "Node can't be attached under provided parentId"),
            @ApiResponse(code = 404, message = "No node for at least one of the provided ids"),
    })
    @PutMapping(path = "/node/{id}")
    @Nonnull
    public ResponseEntity<Object> changeParent(@PathVariable @Nonnull String id, @RequestParam(required = false) @Nullable String parentId) {
        service.updateNodeParent(id, parentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
