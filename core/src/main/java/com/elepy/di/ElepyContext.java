package com.elepy.di;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.*;
import java.util.List;
import java.util.Optional;

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

    default <T> Optional<Constructor<? extends T>> getElepyConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == 1 && (constructor.getParameterTypes()[0].equals(ElepyContext.class))) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }

    default <T> Optional<Constructor<? extends T>> getElepyAnnotatedConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.isAnnotationPresent(ElepyConstructor.class)) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }

    default <T> T initializeElepyObject(Class<? extends T> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        T object = initializeElepyObjectConstructor(cls);
        injectElepyContextFields(object);
        injectFields(object);
        return object;
    }

    default void injectElepyContextFields(Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(ElepyContext.class) && field.get(object) == null) {
                field.set(object, this);
            }
        }
    }

    default Object getObjectForAnnotatedType(AnnotatedElement annotatedType) {
        Inject annotation = annotatedType.getAnnotation(Inject.class);
        final Object contextObject;
        if (annotation.classType().equals(Object.class)) {
            if (annotatedType instanceof Field) {
                if (Crud.class.isAssignableFrom(((Field) annotatedType).getType())) {
                    return this.getSingleton(Crud.class, annotation.tag());
                } else {
                    return this.getSingleton(((Field) annotatedType).getType(), annotation.tag());
                }
            } else if (annotatedType instanceof Parameter) {
                if (Crud.class.isAssignableFrom(((Parameter) annotatedType).getType())) {
                    return this.getSingleton(Crud.class, annotation.tag());
                } else {
                    return this.getSingleton(((Parameter) annotatedType).getType(), annotation.tag());
                }
            }
        }
        return this.getSingleton(annotation.classType(), annotation.tag());

    }

    default void injectFields(Object object) throws IllegalAccessException {
        List<Field> fields = ClassUtils.searchForFieldsWithAnnotation(object.getClass(), Inject.class);

        for (Field field : fields) {
            field.set(object, getObjectForAnnotatedType(field));
        }
    }

    default <T> T initializeElepyObjectConstructor(Class<? extends T> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Optional<Constructor<? extends T>> emptyConstructor =
                ClassUtils.getEmptyConstructor(cls);

        if (emptyConstructor.isPresent()) {
            return emptyConstructor.get().newInstance();
        } else {
            Optional<Constructor<? extends T>> elepyConstructor =
                    getElepyConstructor(cls);
            if (elepyConstructor.isPresent()) {
                return elepyConstructor.get().newInstance(this);
            } else {
                Optional<Constructor<? extends T>> elepyAnnotatedConstructor = getElepyAnnotatedConstructor(cls);

                if (elepyAnnotatedConstructor.isPresent()) {
                    Parameter[] parameters = elepyAnnotatedConstructor.get().getParameters();
                    Object[] dependencies = new Object[parameters.length];
                    System.arraycopy(parameters, 0, dependencies, 0, parameters.length);
                    return elepyAnnotatedConstructor.get().newInstance(dependencies);

                }
            }
        }
        throw new ElepyConfigException(String.format("Can't initialize %s. It has no empty constructor or a constructor with just one ElepyContext.", cls.getName()));

    }

}
