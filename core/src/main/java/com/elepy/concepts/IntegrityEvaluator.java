package com.elepy.concepts;

import com.elepy.dao.Crud;

public interface IntegrityEvaluator<T> {

    void evaluate(T item, Crud<T> dao) throws IllegalAccessException;
}
