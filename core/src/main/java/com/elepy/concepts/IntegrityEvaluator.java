package com.elepy.concepts;

import com.elepy.dao.Crud;

public interface IntegrityEvaluator<T> {

    default void evaluate(T item, Crud<T> dao) {
        evaluate(item, dao, false);
    }

    void evaluate(T item, Crud<T> dao, boolean insert);
}
