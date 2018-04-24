package com.ryansusana.elepy.annotations;

import com.ryansusana.elepy.models.TextType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface Text {
    TextType value() default TextType.TEXTFIELD;

    int minimumLength () default 0;

    int maximumLength() default Integer.MAX_VALUE;
}
