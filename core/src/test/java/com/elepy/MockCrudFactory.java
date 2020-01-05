package com.elepy;

import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.models.Schema;

import static org.mockito.Mockito.mock;

public class MockCrudFactory implements CrudFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Crud<T> crudFor(Schema<T> type) {
        return mock(Crud.class);
    }
}
