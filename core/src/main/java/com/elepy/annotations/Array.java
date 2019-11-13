package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Array {

    /**
     * @deprecated use Java Bean Validation instead
     */
    @Deprecated(forRemoval = true)
    int maximumArrayLength() default 10_000;

    /**
     * @deprecated use Java Bean Validation instead
     */
    @Deprecated(forRemoval = true)
    int minimumArrayLength() default 0;

    boolean sortable() default true;
} 
