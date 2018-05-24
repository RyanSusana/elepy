package com.elepy.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class Schema {


    private final String name;
    private final String slug;
    private final List<Field> fields;

    private final Actions actions;

    @JsonCreator
    public Schema(@JsonProperty("name") String name, @JsonProperty("slug") String slug, @JsonProperty("fields") List<Field> fields, @JsonProperty("actions") Actions actions) {
        this.name = name;
        this.slug = slug;
        this.fields = fields;
        this.actions = actions;
    }

    public String getName() {
        return this.name;
    }

    public String getSlug() {
        return this.slug;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public Actions getActions() {
        return this.actions;
    }
}
