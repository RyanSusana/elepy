package com.ryansusana.elepy.annotations;

import com.ryansusana.elepy.models.NumberType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Number {
    NumberType value() default NumberType.DECIMAL;

    float minimum() default Integer.MIN_VALUE;

    float maximum() default Integer.MAX_VALUE;
}
