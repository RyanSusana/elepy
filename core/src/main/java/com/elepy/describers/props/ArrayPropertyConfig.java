package com.elepy.describers.props;

import com.elepy.describers.Property;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import static com.elepy.models.FieldType.*;

public class ArrayPropertyConfig implements PropertyConfig {
    private final FieldType arrayType;
    private final PropertyConfig arrayConfig;

    public ArrayPropertyConfig(FieldType arrayType, PropertyConfig arrayConfig) {
        this.arrayType = arrayType;
        this.arrayConfig = arrayConfig;
    }


    public static ArrayPropertyConfig of(AccessibleObject field) {
        if (field instanceof Field) {
            final Class arrayGenericType = (Class) ((ParameterizedType) ((Field) field).getGenericType()).getActualTypeArguments()[0];

            final FieldType arrayType = guessByClass(arrayGenericType);

            //Arrays of arrays not allowed
            if (arrayType.equals(ARRAY)) {
                throw new ElepyConfigException(String.format("Collections within Collections are not allowed please rethink the field '%s'", ((Field) field).getName()));
            }

            if (arrayType.equals(NUMBER)) {
                return new ArrayPropertyConfig(arrayType, NumberPropertyConfig.of(field, arrayGenericType));
            } else if (arrayType.equals(TEXT)) {
                return new ArrayPropertyConfig(arrayType, TextPropertyConfig.of(field));
            } else if (arrayType.equals(ENUM)) {
                return new ArrayPropertyConfig(arrayType, EnumPropertyConfig.of(arrayGenericType));
            } else if (arrayType.equals(DATE)) {
                return new ArrayPropertyConfig(arrayType, DatePropertyConfig.of(field));
            } else if (arrayType.equals(BOOLEAN)) {
                return new ArrayPropertyConfig(arrayType, BooleanPropertyConfig.of(field));
            } else if (arrayType.equals(OBJECT)) {
                return new ArrayPropertyConfig(arrayType, ObjectPropertyConfig.of(arrayGenericType));
            }

        } else {
            throw new ElepyConfigException("In Elepy, property collections must be a field. Not a method.");
        }
        throw new ElepyConfigException(String.format("Unable to map the collection '%s'", ((Field) field).getName()));
    }

    @Override
    public void config(Property property) {

        arrayConfig.config(property);
        property.setType(FieldType.ARRAY);
        property.setExtra("arrayType", arrayType);

    }
}
