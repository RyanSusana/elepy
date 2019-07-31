package com.elepy.dao;

import com.elepy.models.Model;

/**
 * This is a class used by Elepy to map a class to a Crud interface
 */
public interface CrudFactory {
    <T> Crud<T> crudFor(Model<T> type);
}
