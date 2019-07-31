package com.elepy.models.props;

import com.elepy.annotations.Array;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.elepy.models.FieldType.*;

public class ArrayPropertyConfig implements PropertyConfig {
    private final boolean sortable;
    private final int maximumArrayLength;
    private final int minimumArrayLength;

    private final FieldType arrayType;
    private final PropertyConfig arrayConfig;

    public ArrayPropertyConfig(boolean sortable, int maximumArrayLength, int minimumArrayLength, FieldType arrayType, PropertyConfig arrayConfig) {
        this.sortable = sortable;
        this.maximumArrayLength = maximumArrayLength;
        this.minimumArrayLength = minimumArrayLength;
        this.arrayType = arrayType;
        this.arrayConfig = arrayConfig;
    }


    public static ArrayPropertyConfig of(AccessibleObject field) {
        if (field instanceof Field) {

            final Array annotation = field.getAnnotation(Array.class);

            final Class arrayGenericType = (Class) ((ParameterizedType) ((Field) field).getGenericType()).getActualTypeArguments()[0];

            final boolean isDefaultSortable = ReflectionUtils.returnTypeOf(field).isAssignableFrom(List.class);

            int maximumArrayLength = annotation == null ? 10_000 : annotation.maximumArrayLength();
            int minimumArrayLength = annotation == null ? 0 : annotation.minimumArrayLength();

            boolean sortable = annotation == null ? isDefaultSortable : annotation.sortable();


            final FieldType arrayType = guessByClass(arrayGenericType);

            //Arrays of arrays not allowed
            if (arrayType.equals(ARRAY)) {
                throw new ElepyConfigException(String.format("Collections within Collections are not allowed please rethink the field '%s'", ((Field) field).getName()));
            }

            if (arrayType.equals(NUMBER)) {
                return new ArrayPropertyConfig(sortable, maximumArrayLength, minimumArrayLength, arrayType, NumberPropertyConfig.of(field, arrayGenericType));
            } else if (arrayType.equals(TEXT)) {
                return new ArrayPropertyConfig(sortable, maximumArrayLength, minimumArrayLength, arrayType, TextPropertyConfig.of(field));
            } else if (arrayType.equals(ENUM)) {
                return new ArrayPropertyConfig(sortable, maximumArrayLength, minimumArrayLength, arrayType, EnumPropertyConfig.of(arrayGenericType));
            } else if (arrayType.equals(DATE)) {
                return new ArrayPropertyConfig(sortable, maximumArrayLength, minimumArrayLength, arrayType, DatePropertyConfig.of(field));
            } else if (arrayType.equals(BOOLEAN)) {
                return new ArrayPropertyConfig(sortable, maximumArrayLength, minimumArrayLength, arrayType, BooleanPropertyConfig.of(field));
            } else if (arrayType.equals(OBJECT)) {
                return new ArrayPropertyConfig(sortable, maximumArrayLength, minimumArrayLength, arrayType, ObjectPropertyConfig.of(arrayGenericType));
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
        property.setExtra("sortable", sortable);
        property.setExtra("maximumArrayLength", maximumArrayLength);
        property.setExtra("minimumArrayLength", minimumArrayLength);
    }
}
