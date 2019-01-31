package com.elepy.dao;

import com.elepy.di.ElepyContext;


/**
 * This is a class used by Elepy to map a class to a Crud interface
 *
 * @see com.elepy.dao.jongo.MongoProvider
 */
public interface CrudProvider {
    <T> Crud<T> crudFor(Class<T> type, ElepyContext elepy);
}
