package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Signifies that this field extends the {@link java.lang.Number} class and it has extra functionality.
 * Can be used with most numbers, including ints, floats, doubles and {@link java.math.BigDecimal}.
 * Boxed or unboxed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Number {
    /**
     * @return How small can this number be
     */
    float minimum() default Integer.MIN_VALUE;

    /**
     * @return How big can this number be
     */
    float maximum() default Integer.MAX_VALUE;
}
