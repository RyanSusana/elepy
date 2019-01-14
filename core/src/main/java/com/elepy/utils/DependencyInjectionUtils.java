package com.elepy.utils;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyConfigException;

import java.lang.reflect.*;
import java.util.List;
import java.util.Optional;

import static com.elepy.utils.ClassUtils.searchForFieldsWithAnnotation;

public class DependencyInjectionUtils {
    private DependencyInjectionUtils() {
    }


    public static <T> Optional<Constructor<? extends T>> getElepyConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.getParameterCount() == 1 && (constructor.getParameterTypes()[0].equals(ElepyContext.class))) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<Constructor<? extends T>> getElepyAnnotatedConstructor(Class<?> cls) {
        for (Constructor constructor : cls.getConstructors()) {
            if (constructor.isAnnotationPresent(ElepyConstructor.class)) {
                return Optional.of((Constructor<T>) constructor);
            }
        }
        return Optional.empty();
    }

    public static <T> T initializeElepyObject(Class<? extends T> cls, ElepyContext elepy) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        T object = initializeElepyObjectConstructor(cls, elepy);
        injectElepyContextFields(elepy, object);
        injectFields(elepy, object);
        return object;
    }

    private static void injectElepyContextFields(ElepyContext elepyContext, Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(ElepyContext.class) && field.get(object) == null) {
                field.set(object, elepyContext);
            }
        }
    }

    private static Object getObjectForAnnotatedType(AnnotatedElement annotatedType, ElepyContext elepyContext) {
        Inject annotation = annotatedType.getAnnotation(Inject.class);
        final Object contextObject;
        if (annotation.classType().equals(Object.class)) {
            if (annotatedType instanceof Field) {
                if (Crud.class.isAssignableFrom(((Field) annotatedType).getType())) {
                    return elepyContext.getSingleton(Crud.class, annotation.tag());
                } else {
                    return elepyContext.getSingleton(((Field) annotatedType).getType(), annotation.tag());
                }
            } else if (annotatedType instanceof Parameter) {
                if (Crud.class.isAssignableFrom(((Parameter) annotatedType).getType())) {
                    return elepyContext.getSingleton(Crud.class, annotation.tag());
                } else {
                    return elepyContext.getSingleton(((Parameter) annotatedType).getType(), annotation.tag());
                }
            }
        }
        return elepyContext.getSingleton(annotation.classType(), annotation.tag());

    }

    private static void injectFields(ElepyContext elepyContext, Object object) throws IllegalAccessException {
        List<Field> fields = searchForFieldsWithAnnotation(object.getClass(), Inject.class);

        for (Field field : fields) {
            field.set(object, getObjectForAnnotatedType(field, elepyContext));
        }
    }

    private static <T> T initializeElepyObjectConstructor(Class<? extends T> cls, ElepyContext elepy) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Optional<Constructor<? extends T>> emptyConstructor =
                ClassUtils.getEmptyConstructor(cls);

        if (emptyConstructor.isPresent()) {
            return emptyConstructor.get().newInstance();
        } else {
            Optional<Constructor<? extends T>> elepyConstructor =
                    getElepyConstructor(cls);
            if (elepyConstructor.isPresent()) {
                return elepyConstructor.get().newInstance(elepy);
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
