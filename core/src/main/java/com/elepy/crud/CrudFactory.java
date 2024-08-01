package com.elepy.crud;

import com.elepy.schemas.Schema;

/**
 * This is a class used by Elepy to map a class to a Crud interface
 */
public interface CrudFactory {
    <T> Crud<T> crudFor(Schema<T> type);
}
