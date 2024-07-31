package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.Dao;
import com.elepy.annotations.DaoFactory;
import com.elepy.annotations.IdProvider;
import com.elepy.dao.Crud;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.models.ModelContext;
import com.elepy.models.Schema;
import com.elepy.utils.Annotations;

public class ModelContextExtraction {

    public static <T> ModelContext<T> extractContext(Schema<T> schema, Elepy elepy) {
        var crud = extractCrud(schema, elepy);
        elepy.registerDependency(Crud.class, schema.getPath(), crud);
        var idProvider = extractIdProvider(schema, elepy);

        return new ModelContext<>(schema, crud, idProvider);
    }

    private static <T> IdentityProvider<T> extractIdProvider(Schema<T> schema, Elepy elepy) {
        var classType = schema.getJavaClass();
        if (classType.isAnnotationPresent(IdProvider.class)) {
            return elepy.initialize(Annotations.get(classType, IdProvider.class).value());
        } else {
            return new DefaultIdentityProvider<>();
        }
    }
    /**
     * Extracts the Crud and returns it
     */
    private static <T> Crud<T> extractCrud(Schema<T> schema, Elepy elepy) {
        var modelType = schema.getJavaClass();
        var annotation = Annotations.get(modelType, DaoFactory.class);

        var crudProvider = annotation == null ?
                elepy.defaultCrudFactory()
                : elepy.initialize(annotation.value());

        final Dao daoAnnotation = Annotations.get(modelType, Dao.class);
        if (daoAnnotation != null) {
            return elepy.initialize(daoAnnotation.value());
        } else {
            return crudProvider.crudFor(schema);
        }
    }
} 
