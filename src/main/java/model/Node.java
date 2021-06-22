package model;

import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public class Node implements Serializable {
    private static final long serialVersionUID = 5990808325362908166L;
    @ApiModelProperty(required = true)
    private String id;
    private String parentId;
    private String rootId;
    private int depth;

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public Node setId(@Nonnull String id) {
        this.id = id;
        return this;
    }

    @Nullable
    public String getParentId() {
        return parentId;
    }

    @Nonnull
    public Node setParentId(@Nullable String parentId) {
        this.parentId = parentId;
        return this;
    }

    @Nonnull
    public String getRootId() {
        return rootId;
    }

    @Nonnull
    public Node setRootId(@Nonnull String rootId) {
        this.rootId = rootId;
        return this;
    }

    public int getDepth() {
        return depth;
    }

    @Nonnull
    public Node setDepth(int depth) {
        this.depth = depth;
        return this;
    }

    public boolean isInvalid() {
        return id == null || id.trim().isEmpty();
    }
}
