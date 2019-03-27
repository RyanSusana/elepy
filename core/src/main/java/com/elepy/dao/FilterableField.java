package com.elepy.dao;

import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;

public class FilterableField {

    private final Field field;
    private final FieldType fieldType;
    private final String name;

    public FilterableField(Class restModelType, String propertyName) {
        this.field = ClassUtils.getPropertyField(restModelType, propertyName);

        if (field == null) {
            throw new ElepyException(String.format("No properties titled '%s'", propertyName));
        }
        this.fieldType = FieldType.guessType(field);
        this.name = ClassUtils.getPropertyName(field);
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}
