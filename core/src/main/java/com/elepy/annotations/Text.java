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
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Text {
    TextType value() default TextType.TEXTFIELD;

    /**
     * @return Minimum amount of characters per this field, per object.
     */
    int minimumLength() default 0;

    /**
     * @return Minimum amount of characters per this field, per object.
     */
    int maximumLength() default Integer.MAX_VALUE;
}
