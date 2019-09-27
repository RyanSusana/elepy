package com.elepy.igniters;

import com.elepy.Elepy;
import com.elepy.annotations.Dao;
import com.elepy.annotations.DaoFactory;
import com.elepy.annotations.Evaluators;
import com.elepy.annotations.IdProvider;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultObjectEvaluator;
import com.elepy.evaluators.ObjectEvaluator;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.models.Model;
import com.elepy.models.ModelContext;

import java.util.ArrayList;
import java.util.List;

public class ModelContextExtraction {

    public static <T> ModelContext<T> extractContext(Model<T> model, Elepy elepy) {
        var crud = extractCrud(model, elepy);
        var objectEvaluators = extractEvaluators(model, elepy);
        var idProvider = extractIdProvider(model, elepy);

        elepy.registerDependency(Crud.class, model.getSlug(), crud);
        return new ModelContext<>(model, crud, idProvider, objectEvaluators);
    }

    private static <T> IdentityProvider<T> extractIdProvider(Model<T> model, Elepy elepy) {
        var classType = model.getJavaClass();
        if (classType.isAnnotationPresent(IdProvider.class)) {
            return elepy.initialize(classType.getAnnotation(IdProvider.class).value());
        } else {
            return new DefaultIdentityProvider<>();
        }
    }

    private static <T> List<ObjectEvaluator<T>> extractEvaluators(Model<T> model, Elepy elepy) {

        List<ObjectEvaluator<T>> objectEvaluators = new ArrayList<>();

        final Evaluators annotation = model.getJavaClass().getAnnotation(Evaluators.class);
        objectEvaluators.add(new DefaultObjectEvaluator<>());

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
    private static <T> Crud<T> extractCrud(Model<T> model, Elepy elepy) {
        var modelType = model.getJavaClass();
        var annotation = modelType.getAnnotation(DaoFactory.class);

        var crudProvider = annotation == null ?
                elepy.defaultCrudFactory()
                : elepy.initialize(annotation.value());

        final Dao daoAnnotation = modelType.getAnnotation(Dao.class);
        if (daoAnnotation != null) {
            return elepy.initialize(daoAnnotation.value());
        } else {
            return crudProvider.crudFor(model);
        }
    }
} 
