package com.elepy.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Field {
    private final String name;
    private final FieldType type;
    private final boolean required;

    public Field(@JsonProperty("name") String name, @JsonProperty("type") FieldType type, @JsonProperty("required") boolean required) {
        this.name = name;
        this.type = type;
        this.required = name.equals("id") || required;
    }

    public String getName() {
        return this.name;
    }

    public FieldType getType() {
        return this.type;
    }

    public boolean isRequired() {
        return this.required;
    }
}
