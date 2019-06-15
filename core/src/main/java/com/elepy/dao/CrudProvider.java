package com.elepy.dao;

import com.elepy.describers.Model;

/**
 * This is a class used by Elepy to map a class to a Crud interface
 */
public interface CrudProvider {
    <T> Crud<T> crudFor(Model<T> type);
}
