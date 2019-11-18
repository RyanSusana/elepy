package com.elepy.annotations;

import com.elepy.models.TextType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Signifies that this field is a String with extra functionality.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE})
public @interface Text {
    TextType value() default TextType.TEXTFIELD;

    /**
     * @deprecated use Java Bean Validation instead
     */
    @Deprecated(forRemoval = true)
    int minimumLength() default 0;

    /**
     * @deprecated use Java Bean Validation instead
     */
    @Deprecated(forRemoval = true)
    int maximumLength() default Integer.MAX_VALUE;
}
