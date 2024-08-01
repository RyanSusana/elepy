package com.elepy.schemas;


import com.elepy.annotations.Number;
import com.elepy.annotations.*;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.Annotations;
import com.google.common.primitives.Primitives;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public enum FieldType {
    ENUM(Enum.class),
    BOOLEAN(Boolean.class),
    DATE(Date.class),
    INPUT(String.class),
    NUMBER(java.lang.Number.class),
    ARRAY(Collection.class),
    OBJECT(Object.class),

    FILE_REFERENCE,
    TEXTAREA,
    MARKDOWN,
    HTML,
    REFERENCE,
    CUSTOM,
    DYNAMIC;

    private final Class<?> baseClass;

    private static final Map<Class<? extends Annotation>, FieldType> annotationMap = Map
            .of(
                    Input.class, INPUT,
                    TextArea.class, TEXTAREA,
                    Markdown.class, MARKDOWN,
                    com.elepy.annotations.HTML.class, HTML,
                    com.elepy.annotations.FileReference.class, FILE_REFERENCE,
                    Number.class, NUMBER,
                    Reference.class, REFERENCE,
                    Custom.class, CUSTOM
            );

    FieldType() {
        this(null);
    }

    FieldType(Class<?> baseClass) {
        this.baseClass = baseClass;
    }


    public static FieldType guessFieldType(AnnotatedElement property) {
        if (property instanceof Field) {
            return guessByField((Field) property);
        } else if (property instanceof Method) {
            return guessByMethod((Method) property);
        } else if (property instanceof AnnotatedType) {
            return guessByAnnotatedType((AnnotatedType) property);
        } else if (property instanceof Class) {
            return guessByClass((Class<?>) property);
        }

        throw new ElepyConfigException("AnnotatedElement must be a Field, Method, Class, or Generic Type");
    }

    private static FieldType guessByAnnotatedType(AnnotatedType annotatedType) {
        return getByAnnotation(annotatedType).orElse(guessByClass((Class<?>) annotatedType.getType()));
    }

    private static FieldType guessByMethod(Method method) {
        return getByAnnotation(method).orElse(guessByClass(method.getReturnType()));
    }

    private static FieldType guessByField(java.lang.reflect.Field field) {
        if (isCollection(field.getType())) {
            return ARRAY;
        }

        return getByAnnotation(field).orElse(guessByClass(field.getType()));
    }

    private static FieldType guessByClass(Class<?> type) {
        if (type.isEnum()) {
            return ENUM;
        }
        if (isCollection(type)) {
            return ARRAY;
        }

        return getUnannotatedFieldType(type);
    }


    private static boolean isCollection(Class<?> type) {
       if (type.isArray()) {
            return true;
        }
       return Collection.class.isAssignableFrom(type);
    }

    private static Optional<FieldType> getByAnnotation(AnnotatedElement property) {

        final var typeFromAnnotation = annotationMap.keySet().stream()
                .filter(annotationClass -> Annotations.isPresent(property, annotationClass))
                .findFirst()
                .map(annotationMap::get);

        return typeFromAnnotation;
    }

    public static FieldType getUnannotatedFieldType(Class<?> type) {

        //int -> Integer
        type = Primitives.wrap(type);
        for (FieldType fieldType : FieldType.values()) {
            if (type.equals(fieldType.baseClass)) {
                return fieldType;
            }
        }

        if (type.getSuperclass() == null)
            return OBJECT;

        return getUnannotatedFieldType(type.getSuperclass());


    }

    public boolean isPrimitive() {

        FieldType[] primitiveConsideredTypes = {BOOLEAN, DATE, INPUT, NUMBER, ENUM, FILE_REFERENCE};

        for (FieldType primitiveConsideredType : primitiveConsideredTypes) {
            if (this.equals(primitiveConsideredType)) {
                return true;
            }
        }
        return false;
    }
}
