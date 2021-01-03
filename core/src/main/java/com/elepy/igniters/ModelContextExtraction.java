package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.Dao;
import com.elepy.annotations.DaoFactory;
import com.elepy.annotations.Evaluators;
import com.elepy.annotations.IdProvider;
import com.elepy.dao.Crud;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.models.ModelContext;
import com.elepy.models.Schema;
import com.elepy.utils.Annotations;

import java.util.ArrayList;
import java.util.List;

public class ModelContextExtraction {

    public static <T> ModelContext<T> extractContext(Schema<T> schema, Elepy elepy) {
        var crud = extractCrud(schema, elepy);
        elepy.registerDependency(Crud.class, schema.getPath(), crud);
        var objectEvaluators = extractEvaluators(schema, elepy);
        var idProvider = extractIdProvider(schema, elepy);

        return new ModelContext<>(schema, crud, idProvider, objectEvaluators);
    }

    private static <T> IdentityProvider<T> extractIdProvider(Schema<T> schema, Elepy elepy) {
        var classType = schema.getJavaClass();
        if (classType.isAnnotationPresent(IdProvider.class)) {
            return elepy.initialize(Annotations.get(classType, IdProvider.class).value());
        } else {
            return new DefaultIdentityProvider<>();
        }
    }

    private static <T> List<ObjectEvaluator<T>> extractEvaluators(Schema<T> schema, Elepy elepy) {

        List<ObjectEvaluator<T>> objectEvaluators = new ArrayList<>();

        final Evaluators annotation = Annotations.get(schema.getJavaClass(), Evaluators.class);

        if (annotation != null) {
            for (Class<? extends ObjectEvaluator> objectEvaluatorClass : annotation.value()) {
                if (objectEvaluatorClass != null) {
                    final ObjectEvaluator<T> constructor = elepy.initialize(objectEvaluatorClass);
                    objectEvaluators.add(constructor);
                }
            }
        }
        return objectEvaluators;

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
