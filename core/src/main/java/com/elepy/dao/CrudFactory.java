package com.elepy.dao;

import com.elepy.models.Schema;

/**
 * This is a class used by Elepy to map a class to a Crud interface
 */
public interface CrudFactory {
    <T> Crud<T> crudFor(Schema<T> type);
}
