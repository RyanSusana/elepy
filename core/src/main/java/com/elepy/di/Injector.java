package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.*;
import java.util.List;
import java.util.Optional;

import static com.elepy.utils.ReflectionUtils.getDependencyTag;
import static com.elepy.utils.ReflectionUtils.getElepyAnnotatedConstructor;

public class Injector {


    private final ElepyContext elepyContext;

    public Injector(ElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }

    public <T> T initializeAndInject(Class<? extends T> cls) {

        try {
            T object = initializeObjectViaConstructor(cls);
            injectElepyContextFields(object);
            injectFields(object);
            return object;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ElepyConfigException("Failed to instantiate an Elepy Object", e);
        }
    }

    public void injectFields(Object object) {
        List<Field> fields = ReflectionUtils.searchForFieldsWithAnnotation(object.getClass(), Inject.class);

        try {
            for (Field field : fields) {
                if (field.get(object) == null) {
                    field.set(object, getDependencyForAnnotatedElement(field));
                }
            }
        } catch (IllegalAccessException e) {
            throw new ElepyConfigException("Failed to instantiate an Elepy Object", e);
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
        Inject annotation = annotatedType.getAnnotation(Inject.class);
        if (annotation.type().equals(Object.class)) {
            if (annotatedType instanceof Field) {
                if (Crud.class.isAssignableFrom(((Field) annotatedType).getType())) {
                    return elepyContext.getDependency(Crud.class, getDependencyTag(annotatedType));
                } else {
                    return elepyContext.getDependency(((Field) annotatedType).getType(), annotation.tag());
                }
            } else if (annotatedType instanceof Parameter) {
                if (Crud.class.isAssignableFrom(((Parameter) annotatedType).getType())) {
                    return elepyContext.getDependency(Crud.class, getDependencyTag(annotatedType));
                } else {
                    return elepyContext.getDependency(((Parameter) annotatedType).getType(), getDependencyTag(annotatedType));
                }
            }
        }
        return elepyContext.getDependency(annotation.type(), annotation.tag());
    }

    private Object getDependencyFromParameter(Parameter parameter) {

        Inject inject = parameter.getAnnotation(Inject.class);

        if (inject != null) {
            if (Crud.class.isAssignableFrom(parameter.getType())) {
                return elepyContext.getDependency(Crud.class, getDependencyTag(parameter));
            } else {
                return elepyContext.getDependency(parameter.getType(), getDependencyTag(parameter));
            }
        }
        return elepyContext.getDependency(parameter.getType(), null);
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
                        dependencies[i] = getDependencyFromParameter(parameters[i]);
                    }
                    return elepyAnnotatedConstructor.get().newInstance(dependencies);

                }
            }
        }
        throw new ElepyConfigException(String.format("Can't initialize %s. It has no empty constructor or a constructor with just one ElepyContext.", cls.getName()));
    }


} 
