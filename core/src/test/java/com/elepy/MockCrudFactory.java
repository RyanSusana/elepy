package com.elepy;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.schemas.Schema;

import static org.mockito.Mockito.mock;

public class MockCrudFactory implements CrudFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Crud<T> crudFor(Schema<T> type) {
        return mock(Crud.class);
    }
}
