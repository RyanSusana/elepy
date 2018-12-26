package com.elepy.dao;

import com.elepy.Elepy;

public interface CrudProvider<T> {
    Crud<T> crudFor(Class<T> type, Elepy elepy);
}
