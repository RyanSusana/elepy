package com.elepy.annotations;

import com.elepy.Elepy;
import com.elepy.evaluators.ObjectEvaluator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extra links to {@link ObjectEvaluator}s to be used to determine the validity of an object.
 * These will be ran in conjunction with {@link Elepy#baseEvaluator()} to validate objects.
 *
 * @see Elepy#baseEvaluator()
 * @see Elepy#withBaseEvaluator(ObjectEvaluator)
 * @see ObjectEvaluator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Evaluators {
    Class<? extends ObjectEvaluator>[] value() default {};
}
