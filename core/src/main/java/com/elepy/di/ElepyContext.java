package com.elepy.di;

import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

public interface ElepyContext {

    /**
     * Tries to GET a dependency with a null tag
     *
     * @param cls The dependency class
     * @param <T> The dependency type
     * @param tag The tag of the dependency
     * @return the dependency
     */
    <T> T getDependency(Class<T> cls, String tag);

    /**
     * Tries to GET a dependency with a null tag
     *
     * @param cls The dependency class
     * @param <T> The dependency type
     * @return the dependency
     */
    default <T> T getDependency(Class<T> cls) {
        return getDependency(cls, null);
    }

    /**
     * Tries to GET a Crud for a RestModel
     *
     * @param cls The RestModel class
     * @param <T> The RestModel type
     * @return the Crud
     */
    default <T> Crud<T> getCrudFor(Class<T> cls) {
        final RestModel annotation = cls.getAnnotation(RestModel.class);

        if (annotation == null) {
            throw new ElepyConfigException("Resources must have the @RestModel Annotation");
        }

        return (Crud<T>) getDependency(Crud.class, annotation.slug());
    }

    default ObjectMapper objectMapper() {
        return getDependency(ObjectMapper.class);
    }

    Set<ContextKey> getDependencyKeys();

    <T> T initializeElepyObject(Class<? extends T> cls);
}
