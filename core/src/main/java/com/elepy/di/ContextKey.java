package com.elepy.di;

import com.elepy.annotations.RestModel;
import com.elepy.dao.Crud;
import com.elepy.utils.ReflectionUtils;
import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

public class ContextKey<T> {
    private final Class<T> classType;
    private final String tag;

    public ContextKey(Class<T> classType, String tag) {
        this.classType = classType;
        this.tag = tag == null ? "" : tag;
    }

    public static ContextKey<?> forAnnotatedElement(AnnotatedElement element) {

        final Class<?> returnType = ReflectionUtils.returnTypeOf(element);
        if (Crud.class.equals(returnType)) {
            return getTypeArgument(getType(element));

        } else if (Crud.class.isAssignableFrom(returnType)) {
            final var exactSuperType = (ParameterizedType) GenericTypeReflector.getExactSuperType(returnType, Crud.class);

            return getTypeArgument(exactSuperType);
        } else {
            return new ContextKey<>(returnType, null);
        }
    }

    private static ParameterizedType getType(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Parameter) {
            return (ParameterizedType) ((Parameter) annotatedElement).getParameterizedType();
        } else {
            final var annotatedElement1 = (Field) annotatedElement;

            return (ParameterizedType) annotatedElement1.getGenericType();
        }
    }

    private static ContextKey<?> getTypeArgument(ParameterizedType exactSuperType) {
        final var model = (Class<?>) exactSuperType.getActualTypeArguments()[0];
        final RestModel declaredAnnotation = model.getAnnotation(RestModel.class);

        return new ContextKey<>(Crud.class, declaredAnnotation.slug());
    }

    public Class<T> getType() {
        return classType;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextKey<?> that = (ContextKey<?>) o;
        return Objects.equals(classType, that.classType) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classType, tag);
    }
}
