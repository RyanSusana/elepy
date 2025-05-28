package com.elepy.igniters;

import com.elepy.crud.Crud;
import com.elepy.id.IdentityProvider;
import com.elepy.schemas.Schema;

public class ModelDetails<T> {
    private final Schema<T> schema;

    private Crud<T> crud;
    private IdentityProvider identityProvider;

    public ModelDetails(Schema<T> schema,
                        Crud<T> crud,
                        IdentityProvider identityProvider
                        ) {
        this.schema = schema;
        this.crud = crud;
        this.identityProvider = identityProvider;
    }

    public Crud<T> getCrud() {
        return crud;
    }

    public void setCrud(Crud<T> crud) {
        this.crud = crud;
    }

    public Schema<T> getSchema() {
        return schema;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public String getName() {
        return schema.getName();
    }

    public String getPath() {
        return schema.getPath();
    }

    public Class<? extends T> getModelType() {
        return schema.getJavaClass();
    }

    public String getIdField() {
        return schema.getIdProperty();
    }
}
