package com.elepy.schemas.options;

import com.elepy.annotations.Number;
import com.elepy.schemas.NumberType;
import com.elepy.utils.Annotations;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;

public class NumberOptions implements Options {
    private final NumberType numberType;

    public NumberOptions(NumberType numberType) {
        this.numberType = numberType;
    }

    public static NumberOptions of(AnnotatedElement field) {
        final Number annotation = Annotations.get(field, Number.class);
        return new NumberOptions(
                NumberType.guessType(ReflectionUtils.returnTypeOf(field)
                ));
    }

    public NumberType getNumberType() {
        return numberType;
    }
}
