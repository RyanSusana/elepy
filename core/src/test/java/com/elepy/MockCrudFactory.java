package com.elepy;

import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.models.Model;

import static org.mockito.Mockito.mock;

public class MockCrudFactory implements CrudFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Crud<T> crudFor(Model<T> type) {
        return mock(Crud.class);
    }
}
