package com.elepy.models.options;

import com.elepy.annotations.Array;
import com.elepy.annotations.InnerObject;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.elepy.models.FieldType.*;

public class ArrayOptions implements Options {

    private boolean sortable;
    private int maximumArrayLength;
    private int minimumArrayLength;

    private FieldType arrayType;

    @JsonUnwrapped
    private Options genericOptions;

    public ArrayOptions(boolean sortable, int maximumArrayLength, int minimumArrayLength, FieldType arrayType, Options genericOptions) {
        this.sortable = sortable;
        this.maximumArrayLength = maximumArrayLength;
        this.minimumArrayLength = minimumArrayLength;
        this.arrayType = arrayType;
        this.genericOptions = genericOptions;
    }


    public static ArrayOptions of(AccessibleObject field) {
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
                return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, NumberOptions.of(field, arrayGenericType));
            } else if (arrayType.equals(TEXT)) {
                return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, TextOptions.of(field));
            } else if (arrayType.equals(ENUM)) {
                return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, EnumOptions.of(arrayGenericType));
            } else if (arrayType.equals(DATE)) {
                return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, DateOptions.of(field));
            } else if (arrayType.equals(BOOLEAN)) {
                return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, BooleanOptions.of(field));
            } else if (arrayType.equals(OBJECT)) {
                return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, ObjectOptions.of(arrayGenericType, field.getAnnotation(InnerObject.class)));
            }

        } else {
            throw new ElepyConfigException("In Elepy, property collections must be a field. Not a method.");
        }
        throw new ElepyConfigException(String.format("Unable to map the collection '%s'", ((Field) field).getName()));
    }

    public boolean isSortable() {
        return sortable;
    }

    public int getMaximumArrayLength() {
        return maximumArrayLength;
    }

    public int getMinimumArrayLength() {
        return minimumArrayLength;
    }

    public FieldType getArrayType() {
        return arrayType;
    }

    public Options getGenericOptions() {
        return genericOptions;
    }
}
