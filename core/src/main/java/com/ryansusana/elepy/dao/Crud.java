package com.ryansusana.elepy.dao;


import java.util.List;
import java.util.Optional;

public interface Crud<T> {

    List<T> getAll();


    Optional<T> getById(final String id);

    List<T> search(final SearchSetup search);


    void delete(final String id);

    void update(final T item);

    void create(final T item);

    String getId(final T item);

}
