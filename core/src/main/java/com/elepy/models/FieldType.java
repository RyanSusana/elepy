package com.elepy.models;


import com.elepy.annotations.Number;
import com.elepy.annotations.Text;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;

public enum FieldType {
    ENUM(Enum.class), BOOLEAN(Boolean.class), DATE(Date.class), TEXT(String.class), NUMBER(java.lang.Number.class), OBJECT(Object.class), ENUM_ARRAY(Collection.class), OBJECT_ARRAY(Collection.class), PRIMITIVE_ARRAY(Collection.class);


    private final Class<?> baseClass;

    FieldType(Class<?> baseClass) {
        this.baseClass = baseClass;
    }

    public static FieldType getByRepresentation(java.lang.reflect.Field field) {


        if (field.getAnnotation(Text.class) != null) {
            return TEXT;
        }
        if (field.getAnnotation(Number.class) != null) {
            return NUMBER;
        }
        if (field.getType().isEnum()) {
            return ENUM;
        }
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


        return getUnannotatedFieldType(field.getType());

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

    public static FieldType getUnannotatedFieldType(Class<?> type) {

        for (FieldType fieldType : FieldType.values()) {
            if (type.equals(fieldType.baseClass)) {
                return fieldType;
            }
        }

        return getUnannotatedFieldType(type.getSuperclass());

    }
}
