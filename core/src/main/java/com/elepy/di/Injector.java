package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.*;
import java.util.List;
import java.util.Optional;

import static com.elepy.utils.ReflectionUtils.getElepyAnnotatedConstructor;

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
            injectElepyContextFields(object);
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
        List<Field> fields = ReflectionUtils.searchForFieldsWithAnnotation(object.getClass(), Inject.class);


        for (Field field : fields) {
            try {
                if (field.get(object) == null) {
                    field.set(object, getDependencyForAnnotatedElement(field));
                }
            } catch (IllegalAccessException e) {
                throw new ElepyConfigException("Failed to inject dependencies on field: " + field.getName(), e);
            }
        }

    }


    private <T> Optional<Constructor<? extends T>> getElepyConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == 1 && (constructor.getParameterTypes()[0].equals(ElepyContext.class))) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }

    private void injectElepyContextFields(Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(ElepyContext.class) && field.get(object) == null) {
                field.set(object, elepyContext);
            }
        }
    }

    private Object getDependencyForAnnotatedElement(AnnotatedElement annotatedType) {
        final var contextKey = ContextKey.forAnnotatedElement(annotatedType);
        return elepyContext.getDependency(contextKey.getType(), contextKey.getTag());

    }

    private <T> T initializeObjectViaConstructor(Class<? extends T> cls) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Optional<Constructor<? extends T>> emptyConstructor =
                ReflectionUtils.getEmptyConstructor(cls);

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
                    for (int i = 0; i < parameters.length; i++) {
                        dependencies[i] = getDependencyForAnnotatedElement(parameters[i]);
                    }
                    return elepyAnnotatedConstructor.get().newInstance(dependencies);

                }
            }
        }
        throw new ElepyConfigException(String.format("Can't initialize %s. It has no empty constructor or a constructor with just one ElepyContext.", cls.getName()));
    }


}