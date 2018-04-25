package com.ryansusana.elepy.dao;


import com.ryansusana.elepy.concepts.FieldUtils;

import java.util.List;
import java.util.Optional;

public interface Crud<T> {

    List<T> getAll();


    Optional<T> getById(final String id);

    List<T> search(final SearchSetup search);

    List<T> search(final String query, Object... params);

    void delete(final String id);

    void update(final T item);

    void create(final T item);

    default String getId(final T item) {
        return FieldUtils.getId(item);
    }

    long count(String query, Object... parameters);

}
