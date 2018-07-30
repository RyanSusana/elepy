package com.elepy.concepts;

public interface ObjectEvaluator<T> {
    void evaluate(T object, Class<T> cls) throws Exception;
}
