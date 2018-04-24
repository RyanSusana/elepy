package com.ryansusana.elepy.concepts;

public interface ObjectUpdateEvaluator<T> {
    void evaluate(T before, T updated) throws Exception;
}
