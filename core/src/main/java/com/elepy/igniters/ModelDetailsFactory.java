package com.elepy.igniters;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudRegistry;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.schemas.Schema;
import com.elepy.schemas.SchemaRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ModelDetailsFactory {

    @Inject
    private CrudRegistry crudRegistry;

    @Inject
    private SchemaRegistry schemaRegistry;

    public <T> ModelDetails<T> getModelDetailsFor(Class<T> clazz) {
        final Crud<T> crud = crudRegistry.getCrudFor(clazz);
        final Schema<T> schema = schemaRegistry.getSchema(clazz);

        // TODO add in the ability to provide an identity provider
        final IdentityProvider identityProvider = new DefaultIdentityProvider();
        return new ModelDetails<T>(schema, crud, identityProvider);
    }

}
