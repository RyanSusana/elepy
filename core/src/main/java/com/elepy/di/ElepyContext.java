package com.elepy.di;

import com.elepy.annotations.Model;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.Annotations;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;
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
        final Model annotation = Annotations.get(cls, Model.class);

        if (annotation == null) {
            throw new ElepyConfigException("Resources must have the @Model Annotation");
        }

        return (Crud<T>) getDependency(Crud.class, annotation.path());
    }


    default ObjectMapper objectMapper() {
        return getDependency(ObjectMapper.class);
    }


    Set<ContextKey> getDependencyKeys();

    default boolean hasDependency(Class<?> dependency) {
        return getDependencyKeys().stream().anyMatch(contextKey -> dependency.equals(contextKey.getType()));
    }

    <T> T initialize(Class<? extends T> cls);

    @SuppressWarnings("unchecked")
    default <T> T initialize(String className) {
        try {
            return (T) initialize(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new ElepyConfigException(String.format("Missing class '%s', make sure you have your dependencies in order", className));
        }

    }

}
