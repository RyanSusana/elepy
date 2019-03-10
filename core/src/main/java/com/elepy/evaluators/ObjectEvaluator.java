package com.elepy.evaluators;

public interface ObjectEvaluator<T> {
    void evaluate(T object, Class<T> cls) throws Exception;
}
