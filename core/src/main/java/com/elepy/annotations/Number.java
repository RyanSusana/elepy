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
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE})
public @interface Number {
    /**
     * @deprecated use Java Bean Validation instead
     */
    @Deprecated(forRemoval = true)
    float minimum() default Integer.MIN_VALUE;

    /**
     * @deprecated use Java Bean Validation instead
     */
    @Deprecated(forRemoval = true)
    float maximum() default Integer.MAX_VALUE;
}
