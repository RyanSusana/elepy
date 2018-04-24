package com.ryansusana.elepy.models;


import com.fasterxml.jackson.annotation.JsonProperty;


public class Actions {
    private final RestModelAccessType findAll;

    private final RestModelAccessType findOne;

    private final RestModelAccessType delete;

    private final RestModelAccessType update;

    private final RestModelAccessType create;

    public Actions(@JsonProperty("findAll") RestModelAccessType findAll, @JsonProperty("findOne") RestModelAccessType findOne, @JsonProperty("delete") RestModelAccessType delete, @JsonProperty("update") RestModelAccessType update, @JsonProperty("create") RestModelAccessType create) {
        this.findAll = findAll;
        this.findOne = findOne;
        this.delete = delete;
        this.update = update;
        this.create = create;
    }

    public RestModelAccessType getFindAll() {
        return this.findAll;
    }

    public RestModelAccessType getFindOne() {
        return this.findOne;
    }

    public RestModelAccessType getDelete() {
        return this.delete;
    }

    public RestModelAccessType getUpdate() {
        return this.update;
    }

    public RestModelAccessType getCreate() {
        return this.create;
    }
}
