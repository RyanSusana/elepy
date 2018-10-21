package com.elepy.models;


import com.elepy.annotations.Number;
import com.elepy.annotations.Text;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public enum FieldType {
    ENUM(Enum.class), BOOLEAN(Boolean.class), DATE(Date.class), TEXT(String.class), NUMBER(java.lang.Number.class), OBJECT(Object.class), ENUM_ARRAY(Collection.class), OBJECT_ARRAY(Collection.class), PRIMITIVE_ARRAY(Collection.class);


    private final Class<?> baseClass;

    FieldType(Class<?> baseClass) {
        this.baseClass = baseClass;
    }


    public static FieldType guessType(Method field) {
        return getByAnnotation(field).orElse(getByClass(field.getReturnType()));
    }


    public boolean isPrimitive() {

        FieldType[] primitiveConsideredTypes = {BOOLEAN, DATE, TEXT, NUMBER, ENUM};

        for (FieldType primitiveConsideredType : primitiveConsideredTypes) {
            if (this.equals(primitiveConsideredType)) {
                return true;
            }
        }
        return false;
    }

    public static FieldType guessType(java.lang.reflect.Field field) {

        if (isCollection(field.getType())) {
            final Class array = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            if (isPrimitive(array)) {
                return PRIMITIVE_ARRAY;
            } else if (array.isEnum()) {
                return ENUM_ARRAY;
            } else {
                return OBJECT_ARRAY;
            }
        }

        return getByAnnotation(field).orElse(getByClass(field.getType()));
    }

    private static FieldType getByClass(Class<?> type) {
        if (type.isEnum()) {
            return ENUM;
        }

        return getUnannotatedFieldType(type);
    }


    private static boolean isPrimitive(Class<?> type) {
        if (ClassUtils.isPrimitiveOrWrapper(type)) {
            return true;
        }
        final FieldType unannotatedFieldType = getUnannotatedFieldType(type);
        return unannotatedFieldType != OBJECT && unannotatedFieldType != OBJECT_ARRAY && unannotatedFieldType != ENUM;
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

        for (FieldType fieldType : FieldType.values()) {
            if (type.equals(fieldType.baseClass)) {
                return fieldType;
            }
        }

        if (type.getSuperclass() == null)
            return OBJECT;

        return getUnannotatedFieldType(type.getSuperclass());


    }
}
