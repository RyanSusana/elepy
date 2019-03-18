package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that this is a boolean with extra functionality
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface TrueFalse {

    /**
     * @return What to display in the CMS to describe true
     */
    String trueValue() default "true";

    /**
     * @return What to display in the CMS to describe false
     */
    String falseValue() default "false";
}
