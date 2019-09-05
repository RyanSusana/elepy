package com.elepy.models.options;

import com.elepy.annotations.Number;
import com.elepy.models.NumberType;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;

public class NumberOptions implements Options {
    private float minimum;
    private float maximum;
    private NumberType numberType;

    public NumberOptions(float minimum, float maximum, NumberType numberType) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.numberType = numberType;
    }

    public static NumberOptions of(AccessibleObject field) {
        return of(field, ReflectionUtils.returnTypeOf(field));
    }

    //This method  was made to get number configuration from arrays by passing the actual  generic class type
    public static NumberOptions of(AccessibleObject field, Class<?> actualNumberType) {
        final Number annotation = field.getAnnotation(Number.class);
        return new NumberOptions(
                annotation == null ? Integer.MIN_VALUE : annotation.minimum(),
                annotation == null ? Integer.MAX_VALUE : annotation.maximum(),
                NumberType.guessType(actualNumberType)
        );
    }

    public float getMinimum() {
        return minimum;
    }

    public float getMaximum() {
        return maximum;
    }

    public NumberType getNumberType() {
        return numberType;
    }
}
