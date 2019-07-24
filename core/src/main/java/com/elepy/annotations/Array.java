package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Array {

    int maximumArrayLength() default 10_000;

    int minimumArrayLength() default 0;

    boolean sortable() default true;
} 
