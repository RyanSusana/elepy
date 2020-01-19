package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.annotations.Property;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

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
        ReflectionUtils.searchForFieldsWithAnnotation(object.getClass(), Inject.class, Property.class)
                .forEach(field -> {
                    try {

                        field.set(object, getDependencyForAnnotatedElement(field));

                    } catch (IllegalAccessException e) {
                        throw new ElepyConfigException("Failed to inject dependencies on field: " + field.getName(), e);
                    }
                });
    }


    private Object cast(Class<?> wrapper, Object value) {
        if (value == null) {
            return null;
        }
        final Class<?> aClass = ClassUtils.primitiveToWrapper(wrapper);

        final var stringValue = value.toString();
        if (aClass.equals(Boolean.class)) {
            return Boolean.parseBoolean(stringValue);
        } else if (aClass.equals(Integer.class)) {
            return Integer.parseInt(stringValue);
        } else if (aClass.equals(Long.class)) {
            return Long.parseLong(stringValue);
        } else if (aClass.isAssignableFrom(BigDecimal.class)) {
            return new BigDecimal(stringValue);
        }

        return value;
    }

    private Object getDependencyForAnnotatedElement(AnnotatedElement annotatedElement) {
        final Class<?> wrapper = ReflectionUtils.returnTypeOf(annotatedElement);

        if (annotatedElement.isAnnotationPresent(Property.class)) {
            final var property = annotatedElement.getAnnotation(Property.class);
            Object value = Optional.ofNullable(elepyContext.getDependency(Configuration.class)
                    .getInterpolator()
                    .interpolate(property.key()))
                    .orElse(property.defaultValue());

            if (isEmpty(value)) {
                value = null;
            }

            if (property.required() && isEmpty(value)) {
                throw new ElepyConfigException("Failed to resolve property: " + property.key());
            }
            return cast(wrapper, value);
        } else {
            final var contextKey = ContextKey.forAnnotatedElement(annotatedElement);

            return elepyContext.getDependency(contextKey.getType(), contextKey.getTag());
        }
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