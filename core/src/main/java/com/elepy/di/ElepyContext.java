package com.elepy.di;

import com.elepy.annotations.Model;
import com.elepy.crud.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.Annotations;
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

    default ObjectMapper objectMapper() {
        return getDependency(ObjectMapper.class);
    }



    @SuppressWarnings("unchecked")
//    default <T> T initialize(String className) {
//        try {
//            return (T) initialize(Class.forName(className));
//        } catch (ClassNotFoundException e) {
//            throw new ElepyConfigException(String.format("Missing class '%s', make sure you have your dependencies in order", className));
//        }
//
//    }
    <T> Crud<T> getCrudFor(Class<T> cls) ;

}
