package com.elepy.concepts;

import com.elepy.dao.Crud;

public abstract class IdProvider<T> {
    public abstract String getId(T item, Crud<T> dao);
}
