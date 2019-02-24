package com.elepy.id;

import com.elepy.dao.Crud;

public interface IdentityProvider<T> {
    void provideId(T item, Crud<T> dao);
}
