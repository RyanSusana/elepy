package com.ryansusana.elepy.concepts;

import com.ryansusana.elepy.dao.Crud;

public interface IntegrityEvaluator<T> {

    void evaluate(T item, Crud<T> dao) throws IllegalAccessException;
}
