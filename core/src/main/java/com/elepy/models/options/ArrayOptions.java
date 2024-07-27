package com.elepy.models.options;

import com.elepy.annotations.Array;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.models.FieldType;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.lang.reflect.*;
import java.util.List;

import static com.elepy.models.FieldType.ARRAY;
import static com.elepy.models.FieldType.guessFieldType;

public class ArrayOptions<T extends Options> implements Options {

    private final boolean sortable;
    private final FieldType arrayType;

    @JsonUnwrapped
    private final T genericOptions;

    public ArrayOptions(boolean sortable, FieldType arrayType, T genericOptions) {
        this.sortable = sortable;
        this.arrayType = arrayType;
        this.genericOptions = genericOptions;
    }


    public static ArrayOptions of(AnnotatedElement field) {
        return of(field, getArrayOptions(field));
    }

    public static ArrayOptions of(AnnotatedElement field, Options options) {
        if (field instanceof Field) {

            final Array annotation = Annotations.get(field,Array.class);

            final boolean isDefaultSortable = ReflectionUtils.returnTypeOf(field).isAssignableFrom(List.class);

            boolean sortable = annotation == null ? isDefaultSortable : annotation.sortable();

            final AnnotatedType arrayGenericType = ((AnnotatedParameterizedType) ((Field) field).getAnnotatedType()).getAnnotatedActualTypeArguments()[0];
            final FieldType arrayType = guessFieldType(arrayGenericType);
            return new ArrayOptions(sortable,  arrayType, options);

        } else {
            throw new ElepyConfigException("In Elepy, property collections must be a field. Not a method.");
        }
    }

    private  static Options getArrayOptions(AnnotatedElement accessibleObject) {
        final var genericType = (AnnotatedParameterizedType) ((Field) accessibleObject).getAnnotatedType();

        final var annotatedType = genericType.getAnnotatedActualTypeArguments()[0];

        final FieldType arrayType = guessFieldType(annotatedType);

        if (arrayType.equals(ARRAY)) {
            throw new ElepyConfigException("Elepy doesn't support multi-dimensional Collections");
        }
        return new OptionFactory().getOptions(annotatedType, arrayType);
    }

    public boolean isSortable() {
        return sortable;
    }

    public FieldType getArrayType() {
        return arrayType;
    }

    public T getGenericOptions() {
        return genericOptions;
    }
}
