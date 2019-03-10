package com.elepy.evaluators;

public interface ObjectUpdateEvaluator<T> {
    void evaluate(T before, T updated) throws Exception;
}
