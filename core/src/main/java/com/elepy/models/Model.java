package com.elepy.models;

import com.elepy.dao.SortOption;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpAction;

import java.util.List;

public class Model<T> {

    private String name;
    private String slug;
    private Class<T> javaClass;
    private boolean viewableOnCMS;
    private String idProperty;
    private String featuredProperty;
    private List<HttpAction> actions;
    private List<Property> properties;

    private String defaultSortField;
    private SortOption defaultSortDirection;

    private HttpAction findOneAction;
    private HttpAction findManyAction;
    private HttpAction deleteAction;
    private HttpAction updateAction;
    private HttpAction createAction;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public HttpAction getFindOneAction() {
        return findOneAction;
    }

    public void setFindOneAction(HttpAction findOneAction) {
        this.findOneAction = findOneAction;
    }

    public HttpAction getFindManyAction() {
        return findManyAction;
    }

    public void setFindManyAction(HttpAction findManyAction) {
        this.findManyAction = findManyAction;
    }

    public HttpAction getDeleteAction() {
        return deleteAction;
    }

    public void setDeleteAction(HttpAction deleteAction) {
        this.deleteAction = deleteAction;
    }

    public HttpAction getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(HttpAction updateAction) {
        this.updateAction = updateAction;
    }

    public HttpAction getCreateAction() {
        return createAction;
    }

    public void setCreateAction(HttpAction createAction) {
        this.createAction = createAction;
    }

    public boolean isViewableOnCMS() {
        return viewableOnCMS;
    }

    public void setViewableOnCMS(boolean viewableOnCMS) {
        this.viewableOnCMS = viewableOnCMS;
    }
}
