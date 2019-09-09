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

import static com.elepy.models.FieldType.guessByClass;

public class ArrayOptions<T extends Options> implements Options {

    private boolean sortable;
    private int maximumArrayLength;
    private int minimumArrayLength;

    private FieldType arrayType;

    @JsonUnwrapped
    private T genericOptions;

    public ArrayOptions(boolean sortable, int maximumArrayLength, int minimumArrayLength, FieldType arrayType, T genericOptions) {
        this.sortable = sortable;
        this.maximumArrayLength = maximumArrayLength;
        this.minimumArrayLength = minimumArrayLength;
        this.arrayType = arrayType;
        this.genericOptions = genericOptions;
    }


    public static ArrayOptions of(AccessibleObject field) {
        return of(field, getArrayOptions(field));
    }

    public static ArrayOptions of(AccessibleObject field, Options options) {
        if (field instanceof Field) {

            final Array annotation = field.getAnnotation(Array.class);


            final boolean isDefaultSortable = ReflectionUtils.returnTypeOf(field).isAssignableFrom(List.class);

            int maximumArrayLength = annotation == null ? 10_000 : annotation.maximumArrayLength();
            int minimumArrayLength = annotation == null ? 0 : annotation.minimumArrayLength();

            boolean sortable = annotation == null ? isDefaultSortable : annotation.sortable();

            final Class arrayGenericType = (Class) ((ParameterizedType) ((Field) field).getGenericType()).getActualTypeArguments()[0];
            final FieldType arrayType = guessByClass(arrayGenericType);
            return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, options);

        } else {
            throw new ElepyConfigException("In Elepy, property collections must be a field. Not a method.");
        }
    }

    private static Options getArrayOptions(AccessibleObject field) {
        final Class arrayGenericType = (Class) ((ParameterizedType) ((Field) field).getGenericType()).getActualTypeArguments()[0];

        final FieldType arrayType = guessByClass(arrayGenericType);

        switch (arrayType) {
            case NUMBER:
                return NumberOptions.of(field, arrayGenericType);
            case TEXT:
                return TextOptions.of(field);
            case ENUM:
                return EnumOptions.of(arrayGenericType);
            case DATE:
                return DateOptions.of(field);
            case BOOLEAN:
                return BooleanOptions.of(field);
            case OBJECT:
                return ObjectOptions.of(arrayGenericType, field.getAnnotation(InnerObject.class));
            case FILE_REFERENCE:
                return FileReferenceOptions.of(field);
            case ARRAY:
                throw new ElepyConfigException(String.format("Collections within Collections are not allowed please rethink the field '%s'", ((Field) field).getName()));
            default:
                throw new ElepyConfigException(String.format("FieldType '%s' not supported by Collections", arrayType.name()));
        }
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

    public T getGenericOptions() {
        return genericOptions;
    }
}
