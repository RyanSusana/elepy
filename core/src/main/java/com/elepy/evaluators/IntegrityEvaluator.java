package com.elepy.evaluators;

import com.elepy.describers.ModelContext;


/**
 * This evaluator evaluates if an object is valid within it's own {@link ModelContext}.
 */
public interface IntegrityEvaluator<T> {

    default void evaluate(T item) {
        evaluate(item, false);
    }

    void evaluate(T item, boolean isACreate);
}
