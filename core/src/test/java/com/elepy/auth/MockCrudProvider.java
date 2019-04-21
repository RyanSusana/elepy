package com.elepy.auth;

import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;

public class MockCrudProvider implements CrudProvider {
    @Override
    public <T> Crud<T> crudFor(Class<T> type) {
        return null;
    }
}
