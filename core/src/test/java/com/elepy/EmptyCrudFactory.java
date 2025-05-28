package com.elepy;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.query.Expression;
import com.elepy.query.Query;
import com.elepy.schemas.Schema;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EmptyCrudFactory implements CrudFactory {
    @Override
    public <T> Crud<T> crudFor(Schema<T> type) {
        return new Crud<T>() {
            @Override
            public List<T> find(Query query) {
                return List.of();
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
                return List.of();
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
        };
    }
}
