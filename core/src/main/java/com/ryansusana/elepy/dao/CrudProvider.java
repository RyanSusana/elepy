package com.ryansusana.elepy.dao;

import com.ryansusana.elepy.Elepy;

public interface  CrudProvider<T> {


     Crud<T> crudFor(Class<T> type, Elepy elepy);
}
