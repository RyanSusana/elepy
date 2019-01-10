package com.elepy.di;

import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ElepyContext {

    <T> T getSingleton(Class<T> cls, String tag);

    default <T> T getSingleton(Class<T> cls) {
        return getSingleton(cls, null);
    }

    default <T> Crud<T> getCrudFor(Class<T> cls) {
        final RestModel annotation = cls.getAnnotation(RestModel.class);

        if (annotation == null) {
            throw new ElepyConfigException("Resources must have the @RestModel Annotation");
        }

        return (Crud<T>) getSingleton(Crud.class, annotation.slug());
    }


    default ObjectMapper getObjectMapper() {
        return getSingleton(ObjectMapper.class);
    }
}
