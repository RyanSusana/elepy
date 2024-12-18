package com.elepy.di;

import com.elepy.crud.Crud;
import com.elepy.query.Expression;
import com.elepy.query.Query;
import com.elepy.schemas.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class MockCrudGeneric<T> implements Crud<T> {
    @Override
    public List<T> find(Query query) {
        return null;
    }

    @Override
    public Optional<T> getById(Serializable id) {
        return Optional.empty();
    }


    @Override
    public void update(T item) {

    }

    @Override
    public void create(T item) {

    }

    @Override
    public List<T> getAll() {
        return null;
    }

    @Override
    public void deleteById(Serializable id) {

    }

    @Override
    public void delete(Expression expression) {

    }

    @Override
    public long count(Expression query) {
        return 0;
    }

    @Override
    public Schema<T> getSchema() {
        return null;
    }

}
