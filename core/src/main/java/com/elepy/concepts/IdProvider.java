package com.elepy.concepts;

import com.elepy.dao.Crud;

public interface IdProvider<T> {
    String getId(T item, Crud<T> dao);
}
