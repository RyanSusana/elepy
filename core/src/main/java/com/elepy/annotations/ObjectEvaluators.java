package com.elepy.annotations;

import com.elepy.concepts.ObjectEvaluator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ObjectEvaluators {
    Class<? extends ObjectEvaluator>[] value() default {};
}
