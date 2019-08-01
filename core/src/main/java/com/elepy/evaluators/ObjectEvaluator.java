package com.elepy.evaluators;


/**
 * This evaluator evaluates if an object is valid.
 */
public interface ObjectEvaluator<T> {
    void evaluate(T object) throws Exception;
}
