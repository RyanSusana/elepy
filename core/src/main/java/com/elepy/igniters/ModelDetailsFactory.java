package com.elepy.igniters;

import com.elepy.annotations.IdProvider;
import com.elepy.annotations.Model;
import com.elepy.crud.Crud;
import com.elepy.crud.CrudRegistry;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.schemas.Schema;
import com.elepy.schemas.SchemaRegistry;
import com.elepy.utils.Annotations;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class ModelDetailsFactory {

    @Inject
    private CrudRegistry crudRegistry;

    @Inject
    private SchemaRegistry schemaRegistry;

    public <T> ModelDetails<T> getModelDetailsFor(Class<T> clazz) {
        final Crud<T> crud = crudRegistry.getCrudFor(clazz);
        final Schema<T> schema = schemaRegistry.getSchema(clazz);
        var providedIdProvider = Optional.ofNullable(clazz.getAnnotation(IdProvider.class))
                .map(IdProvider::value);

        final IdentityProvider identityProvider;
        if (providedIdProvider.isPresent()) {
            identityProvider = CDI.current().select(providedIdProvider.get()).get();
        } else {
            identityProvider = new DefaultIdentityProvider();
        }
        return new ModelDetails<>(schema, crud, identityProvider);
    }

}
