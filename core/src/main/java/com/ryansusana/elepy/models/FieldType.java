package com.ryansusana.elepy.models;


import com.ryansusana.elepy.annotations.Number;
import com.ryansusana.elepy.annotations.Text;

import java.util.Date;

public enum FieldType {
    ENUM(Enum.class), BOOLEAN(Boolean.class), DATE(Date.class), TEXT(String.class), NUMBER(java.lang.Number.class), OBJECT(Object.class);


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

        return getUnannotatedFieldType(field.getType());

    }

    private static FieldType getUnannotatedFieldType(Class<?> type) {
        for (FieldType fieldType : FieldType.values()) {
            if (type.equals(fieldType.baseClass)) {
                return fieldType;
            }
        }
        return getUnannotatedFieldType(type.getSuperclass());

    }
}
