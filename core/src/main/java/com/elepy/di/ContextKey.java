package com.elepy.di;

import com.elepy.annotations.Model;
import com.elepy.crud.Crud;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;
import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.*;
import java.util.Objects;

public class ContextKey<T> {
    private final Class<T> classType;
    private final String tag;

    ContextKey(Class<T> classType, String tag) {
        this.classType = classType;
        this.tag = tag == null ? "" : tag;
    }

    public Class<T> getType() {
        return classType;
    }

    public String getTag() {
        return tag;
    }

    static ContextKey<?> forAnnotatedElement(AnnotatedElement element) {
        final Class<?> returnType = ReflectionUtils.returnTypeOf(element);

        if (Crud.class.equals(returnType)) {
            return getTypeArgument(toParameterizedType(element));
        } else if (Crud.class.isAssignableFrom(returnType)) {
            final var exactSuperType = (ParameterizedType) GenericTypeReflector
                    .getExactSuperType(
                            getGenericType(element),
                            Crud.class);

            return getTypeArgument(exactSuperType);
        } else {
            return new ContextKey<>(returnType, null);
        }
    }

    private static Type getGenericType(AnnotatedElement element) {
        if (element instanceof Field) {
            return ((Field) element).getGenericType();
        } else {
            final var parameter = (Parameter) element;

            return parameter.getParameterizedType();
        }
    }

    private static ParameterizedType toParameterizedType(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Parameter) {
            return (ParameterizedType) ((Parameter) annotatedElement).getParameterizedType();
        } else {
            final var annotatedElement1 = (Field) annotatedElement;

            return (ParameterizedType) annotatedElement1.getGenericType();
        }
    }

    private static ContextKey<?> getTypeArgument(ParameterizedType exactSuperType) {
        final var model = (Class<?>) exactSuperType.getActualTypeArguments()[0];
        final Model declaredAnnotation = Annotations.get(model, Model.class);

        return new ContextKey<>(Crud.class, declaredAnnotation == null ? null : declaredAnnotation.path());
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
