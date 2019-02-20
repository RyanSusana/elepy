package com.elepy.models;

import com.elepy.concepts.IdentityProvider;
import com.elepy.concepts.ObjectEvaluator;

import java.util.List;

public class ModelDescription<T> {

    private final Class<T> modelType;
    private final IdentityProvider<T> identityProvider;
    private final List<ObjectEvaluator<T>> objectEvaluators;
    private final String slug;
    private final String name;


    public ModelDescription(String slug, String name, Class<T> modelType, IdentityProvider<T> identityProvider, List<ObjectEvaluator<T>> objectEvaluators) {
        this.modelType = modelType;
        this.identityProvider = identityProvider;
        this.objectEvaluators = objectEvaluators;
        this.slug = slug;
        this.name = name;
    }


    public Class<T> getModelType() {
        return modelType;
    }

    public IdentityProvider<T> getIdentityProvider() {
        return identityProvider;
    }

    public List<ObjectEvaluator<T>> getObjectEvaluators() {
        return objectEvaluators;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }
}
