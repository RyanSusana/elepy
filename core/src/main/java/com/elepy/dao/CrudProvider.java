package com.elepy.dao;

/**
 * This is a class used by Elepy to map a class to a Crud interface
 *
 */
public interface CrudProvider {
    <T> Crud<T> crudFor(Class<T> type);
}
