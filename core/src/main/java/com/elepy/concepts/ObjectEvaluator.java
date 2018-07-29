package com.elepy.concepts;

public interface ObjectEvaluator<T> {
    void evaluate(Object object, Class<T> cls) throws Exception;
}
