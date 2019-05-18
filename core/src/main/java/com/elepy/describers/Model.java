package com.elepy.describers;

import com.elepy.http.HttpAction;

import java.util.List;
import java.util.Set;

public class Model {


    private String name;
    private String slug;
    private String javaClass;
    private String idField;
    private List<HttpAction> actions;
    private List<HttpAction> defaultActions;
    private Set<Property> properties;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public List<HttpAction> getActions() {
        return actions;
    }

    public void setActions(List<HttpAction> actions) {
        this.actions = actions;
    }

    public List<HttpAction> getDefaultActions() {
        return defaultActions;
    }

    public void setDefaultActions(List<HttpAction> defaultActions) {
        this.defaultActions = defaultActions;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }
}
