package com.elepy.models;

import com.elepy.dao.Crud;
import com.elepy.id.IdentityProvider;

public class ModelContext<T> {
    private final Schema<T> schema;

    private Crud<T> crud;
    private IdentityProvider<T> identityProvider;

    public ModelContext(Schema<T> schema,
                        Crud<T> crud,
                        IdentityProvider<T> identityProvider
                        ) {
        this.schema = schema;
        this.crud = crud;
        this.identityProvider = identityProvider;
    }

    public <C extends Crud<T>> C getCrud() {
        return (C) crud;
    }

    public void setCrud(Crud<T> crud) {
        this.crud = crud;
    }

    public void changeModel(ModelChange modelChange) {
        modelChange.change(this);
    }

    public Schema<T> getSchema() {
        return schema;
    }

    public IdentityProvider<T> getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider<T> identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String getName() {
        return schema.getName();
    }

    public String getPath() {
        return schema.getPath();
    }

    public Class<T> getModelType() {
        return schema.getJavaClass();
    }

    public String getIdField() {
        return schema.getIdProperty();
    }
}
