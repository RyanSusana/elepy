package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

class Injector {


    private final ElepyContext elepyContext;

    Injector(ElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }

    /**
     * Initializes  a class  while injecting dependencies.
     *
     * @param cls The class to initialize
     * @param <T> The return type
     * @return an initialized version of the class
     */
    <T> T initializeAndInject(Class<? extends T> cls) {

        try {
            T object = initializeObjectViaConstructor(cls);
            injectFields(object);
            return object;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ElepyConfigException("Failed to instantiate an Elepy Object", e);
        }
    }


    /**
     * Injects all the field of an object that are null  and are  annotated with {@link Inject}
     *
     * @param object the object to inject
     */
    void injectFields(Object object) {
        ReflectionUtils.searchForFieldsWithAnnotation(object.getClass(), Inject.class)
                .forEach(field -> {
                    try {
                        if (field.get(object) == null) {
                            field.set(object, getDependencyForAnnotatedElement(field));
                        }
                    } catch (IllegalAccessException e) {
                        throw new ElepyConfigException("Failed to inject dependencies on field: " + field.getName(), e);
                    }
                });

    }

    private Object getDependencyForAnnotatedElement(AnnotatedElement annotatedType) {
        final var contextKey = ContextKey.forAnnotatedElement(annotatedType);

        return elepyContext.getDependency(contextKey.getType(), contextKey.getTag());
    }

    private <T> T initializeObjectViaConstructor(Class<? extends T> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends T> constructor = ReflectionUtils.getElepyConstructor(cls)
                .orElseThrow(() -> new ElepyConfigException(String.format("Can't initialize %s. It has no empty constructor or a constructor with just one ElepyContext.", cls.getName())));

        Parameter[] parameters = constructor.getParameters();
        Object[] dependencies = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            dependencies[i] = getDependencyForAnnotatedElement(parameters[i]);
        }

        return constructor.newInstance(dependencies);
    }
}