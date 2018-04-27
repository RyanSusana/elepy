package com.ryansusana.elepy.dao;


import com.ryansusana.elepy.utils.ClassUtils;

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
        Optional<String> id = ClassUtils.getId(item);

        if (id.isPresent()) {
            return id.get();
        }
        throw new IllegalStateException(item.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");

    }

    long count(String query, Object... parameters);

}
