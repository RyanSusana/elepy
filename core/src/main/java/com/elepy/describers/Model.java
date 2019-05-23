package com.elepy.describers;

import com.elepy.dao.SortOption;
import com.elepy.http.HttpAction;

import java.util.List;
import java.util.Set;

public class Model<T> {

    private String name;
    private String slug;
    private Class<T> javaClass;
    private String idField;
    private List<HttpAction> actions;
    private Set<Property> properties;

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

    public Class<T> getJavaClass() {
        return javaClass;
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

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }

    public void setJavaClass(Class<T> javaClass) {
        this.javaClass = javaClass;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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
}
