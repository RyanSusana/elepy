package com.elepy.dao;

import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public interface Crud<T> {

    Page<T> search(QuerySetup querySetup);

    Optional<T> getById(final String id);

    List<T> searchInField(Field field, String qry);


    void update(final T item);

    void create(final T item);


    default String getId(final T item) {
        Optional<String> id = ClassUtils.getId(item);

        if (id.isPresent()) {
            return id.get();
        }
        throw new IllegalStateException(item.getClass().getName() + ": has no annotation id. You must annotate the class with MongoId and if no id generator is specified, you must generate your own.");

    }

    default void update(final Iterable<T> items) {
        for (T item : items) {
            update(item);
        }
    }


    default void create(final Iterable<T> items) {
        for (T item : items) {
            create(items);
        }
    }



    long count(String query);
    default long count(){
        return count("");
    }

    Class<T> getType();

    void delete(final String id);

    void deleteQuery(String pattern, Object... params);

    default void deleteAll() {
        deleteQuery("{}");
    }
}
