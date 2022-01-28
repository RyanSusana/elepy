package com.elepy.models.options;

import com.elepy.annotations.Array;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;
import com.elepy.utils.ModelUtils;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.lang.reflect.*;
import java.util.List;

import static com.elepy.models.FieldType.ARRAY;
import static com.elepy.models.FieldType.guessFieldType;

public class ArrayOptions<T extends Options> implements Options {

    private final boolean sortable;
    private final int maximumArrayLength;
    private final int minimumArrayLength;

    private final FieldType arrayType;

    @JsonUnwrapped
    private final T genericOptions;

    public ArrayOptions(boolean sortable, int maximumArrayLength, int minimumArrayLength, FieldType arrayType, T genericOptions) {
        this.sortable = sortable;
        this.maximumArrayLength = maximumArrayLength;
        this.minimumArrayLength = minimumArrayLength;
        this.arrayType = arrayType;
        this.genericOptions = genericOptions;
    }


    public static ArrayOptions of(AnnotatedElement field) {
        return of(field, getArrayOptions(field));
    }

    public static ArrayOptions of(AnnotatedElement field, Options options) {
        if (field instanceof Field) {

            final Array annotation = com.elepy.utils.Annotations.get(field,Array.class);


            final boolean isDefaultSortable = ReflectionUtils.returnTypeOf(field).isAssignableFrom(List.class);

            int maximumArrayLength = annotation == null ? 10_000 : annotation.maximumArrayLength();
            int minimumArrayLength = annotation == null ? 0 : annotation.minimumArrayLength();

            boolean sortable = annotation == null ? isDefaultSortable : annotation.sortable();

            final AnnotatedType arrayGenericType = ((AnnotatedParameterizedType) ((Field) field).getAnnotatedType()).getAnnotatedActualTypeArguments()[0];
            final FieldType arrayType = guessFieldType(arrayGenericType);
            return new ArrayOptions(sortable, maximumArrayLength, minimumArrayLength, arrayType, options);

        } else {
            throw new ElepyConfigException("In Elepy, property collections must be a field. Not a method.");
        }
    }

    private static Options getArrayOptions(AnnotatedElement accessibleObject) {
        final var genericType = (AnnotatedParameterizedType) ((Field) accessibleObject).getAnnotatedType();

        final var annotatedType = genericType.getAnnotatedActualTypeArguments()[0];

        final FieldType arrayType = guessFieldType(annotatedType);

        if (arrayType.equals(ARRAY)) {
            throw new ElepyConfigException("Elepy doesn't support multi-dimensional Collections");
        }
        return ModelUtils.getOptions(annotatedType, arrayType);

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
