package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that this field is a Date.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DateTime {

    /**
     * @return true if you want to display the time in the CMS as well
     */
    boolean includeTime() default true;

    /**
     * The minimum date. It supports a list of formats including UNIX timestamps, ISO time and simple 'YYYY-MM-dd''
     */
    String minimumDate() default "1970-01-01";

    /**
     * The maximum date. It supports a list of formats including UNIX timestamps, ISO time and simple 'YYYY-MM-dd''
     */
    String maximumDate() default "2099-12-22";
}
