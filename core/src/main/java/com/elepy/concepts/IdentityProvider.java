package com.elepy.concepts;

import com.elepy.dao.Crud;

public interface IdentityProvider<T> {
    Object getId(T item, Crud<T> dao);
}
