package com.elepy.annotations;

import com.elepy.Elepy;
import com.elepy.evaluators.ObjectEvaluator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated use Java Bean Validation instead
 */
@Deprecated(forRemoval = true)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Evaluators {
    Class<? extends ObjectEvaluator>[] value() default {};
}
