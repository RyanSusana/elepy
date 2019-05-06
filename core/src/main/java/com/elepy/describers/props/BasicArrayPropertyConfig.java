package com.elepy.describers.props;

import com.elepy.describers.Property;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class BasicArrayPropertyConfig implements PropertyConfig {
    private final FieldType arrayType;

    public BasicArrayPropertyConfig(FieldType arrayType) {
        this.arrayType = arrayType;
    }

    public static BasicArrayPropertyConfig of(AccessibleObject field) {
        if (field instanceof Field) {
            final Class array = (Class) ((ParameterizedType) ((Field) field).getGenericType()).getActualTypeArguments()[0];
            return new BasicArrayPropertyConfig(FieldType.getUnannotatedFieldType(array));
        }
        throw new ElepyConfigException("In Elepy, arrays must be a field. Not a method.");
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.PRIMITIVE_ARRAY);
        property.setExtra("arrayType", arrayType);
    }
}
