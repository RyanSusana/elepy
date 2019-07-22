package com.elepy.models;


import com.elepy.annotations.Number;
import com.elepy.annotations.Text;
import com.elepy.uploads.FileReference;
import com.google.common.primitives.Primitives;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public enum FieldType {
    ENUM(Enum.class),
    BOOLEAN(Boolean.class),
    DATE(Date.class),
    TEXT(String.class),
    NUMBER(java.lang.Number.class),
    OBJECT(Object.class),
    ARRAY(Collection.class),
    FILE_REFERENCE(FileReference.class);


    private final Class<?> baseClass;

    FieldType(Class<?> baseClass) {
        this.baseClass = baseClass;
    }


    public static FieldType guessType(AccessibleObject property) {
        if (property instanceof Field) {
            return guessType((Field) property);
        } else {
            return guessType((Method) property);
        }
    }

    public static FieldType guessType(Method method) {
        return getByAnnotation(method).orElse(guessByClass(method.getReturnType()));
    }

    public static FieldType guessType(java.lang.reflect.Field field) {
        if (isCollection(field.getType())) {
            return ARRAY;
        }

        return getByAnnotation(field).orElse(guessByClass(field.getType()));
    }

    public static FieldType guessByClass(Class<?> type) {
        if (type.isEnum()) {
            return ENUM;
        }

        return getUnannotatedFieldType(type);
    }



    private static boolean isCollection(Class<?> type) {
        for (Class<?> aClass : type.getInterfaces()) {
            if (aClass.equals(Collection.class)) {
                return true;
            }
        }
        return false;
    }

    private static Optional<FieldType> getByAnnotation(AccessibleObject accessibleObject) {
        if (accessibleObject.getAnnotation(Text.class) != null) {
            return Optional.of(TEXT);
        }
        if (accessibleObject.getAnnotation(Number.class) != null) {
            return Optional.of(NUMBER);
        }
        return Optional.empty();
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

        FieldType[] primitiveConsideredTypes = {BOOLEAN, DATE, TEXT, NUMBER, ENUM, FILE_REFERENCE};

        for (FieldType primitiveConsideredType : primitiveConsideredTypes) {
            if (this.equals(primitiveConsideredType)) {
                return true;
            }
        }
        return false;
    }
}
