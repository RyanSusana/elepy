package com.elepy.evaluators;

/**
 * This evaluator evaluates if an object is valid within in respects to an {@link EvaluationType}.
 */
public interface IntegrityEvaluator<T> {
    void evaluate(T item, EvaluationType type) throws Exception;
}
