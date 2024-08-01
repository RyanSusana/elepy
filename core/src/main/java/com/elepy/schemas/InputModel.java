package com.elepy.schemas;

import java.util.List;

public class InputModel<T> {

    private String description;
    private List<Property> properties;
    private Class<T> javaClass;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Class<T> getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class<T> javaClass) {
        this.javaClass = javaClass;
    }
}
