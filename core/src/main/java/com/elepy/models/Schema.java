package com.elepy.models;

import com.elepy.dao.SortOption;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpAction;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema<T> {

    private String name;
    private String path;
    private Class<T> javaClass;
    private boolean viewableOnCMS;
    private String idProperty;
    private String featuredProperty;
    private List<HttpAction> actions;
    private List<Property> properties;

    private String view;

    private String defaultSortField;
    private SortOption defaultSortDirection;
    private int keepRevisions = 0;

    private Map<String, HttpAction> defaultActions = new HashMap<>();


    public Map<String, HttpAction> getDefaultActions() {
        return defaultActions;
    }

    public void setDefaultActions(Map<String, HttpAction> defaultActions) {
        this.defaultActions = defaultActions;
    }

    public String getDefaultSortField() {
        return defaultSortField;
    }

    public void setDefaultSortField(String defaultSortField) {
        this.defaultSortField = defaultSortField;
    }

    public String getFeaturedProperty() {
        return featuredProperty;
    }

    public void setFeaturedProperty(String featuredProperty) {
        this.featuredProperty = featuredProperty;
    }

    public SortOption getDefaultSortDirection() {
        return defaultSortDirection;
    }

    public void setDefaultSortDirection(SortOption defaultSortDirection) {
        this.defaultSortDirection = defaultSortDirection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class<T> getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class<T> javaClass) {
        this.javaClass = javaClass;
    }

    public String getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }

    public List<HttpAction> getActions() {
        return actions;
    }

    public void setActions(List<HttpAction> actions) {
        this.actions = actions;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }


    public Property getProperty(String name) {
        return this.getProperties()
                .stream()
                .filter(property -> name.equals(property.getName()))
                .findFirst().orElseThrow(() -> new ElepyConfigException("No property with the name: " + name));
    }

    public boolean isViewableOnCMS() {
        return viewableOnCMS;
    }

    public void setViewableOnCMS(boolean viewableOnCMS) {
        this.viewableOnCMS = viewableOnCMS;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public int getKeepRevisions() {
        return keepRevisions;
    }
}
