package com.ryansusana.elepy.annotations;

import com.ryansusana.elepy.concepts.ObjectEvaluator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface RestEvaluator {
    Class<? extends ObjectEvaluator> value();
}
