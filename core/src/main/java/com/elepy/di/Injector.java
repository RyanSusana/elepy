package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.annotations.Property;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.ReflectionUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

class Injector {


    private final ElepyContext elepyContext;

    private static final Logger logger = LoggerFactory.getLogger(Injector.class);


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
            throw new ElepyConfigException("Failed to instantiate an Elepy Object: " + cls
                    .getName(), e);
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
                        final var dependencyForAnnotatedElement = getDependencyForAnnotatedElement(field);

                        if (dependencyForAnnotatedElement != null) {
                            field.set(object, dependencyForAnnotatedElement);
                        }

                    } catch (IllegalAccessException e) {
                        throw new ElepyConfigException("Failed to inject dependencies on field: " + field.getName(), e);
                    }
                });
    }


    private Object getDependencyForAnnotatedElement(AnnotatedElement annotatedElement) {
        final Class<?> wrapper = ReflectionUtils.returnTypeOf(annotatedElement);

        if (annotatedElement.isAnnotationPresent(Property.class)) {


            final var property = annotatedElement.getAnnotation(Property.class);
            return getProp(wrapper, property);


        } else {
            final var contextKey = ContextKey.forAnnotatedElement(annotatedElement);

            return elepyContext.getDependency(contextKey.getType(), contextKey.getTag());
        }
    }

    private Object getProp(Class<?> returnType, Property annotation) {

        final Class<?> primitiveWrapper = ClassUtils.primitiveToWrapper(returnType);
        StringConverter stringConverter = new StringConverter();

        final var configuration = elepyContext.getDependency(Configuration.class);

        try {
            final Object o = configuration.get(primitiveWrapper, annotation.key());

            if (o != null) {
                return o;
            }

            if (isEmpty(annotation.defaultValue())) {
                return null;
            }

            return stringConverter.convert(primitiveWrapper, annotation.defaultValue());
        } catch (ConversionException e) {
            logger.error(e.getMessage(), e);
            return null;
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