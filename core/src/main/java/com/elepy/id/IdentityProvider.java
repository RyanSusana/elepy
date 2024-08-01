package com.elepy.id;

import com.elepy.crud.Crud;

public interface IdentityProvider<T> {
    void provideId(T item, Crud<T> dao);
}
