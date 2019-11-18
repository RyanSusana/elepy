package com.elepy.di;

import com.elepy.Resource;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.PageSettings;
import com.elepy.dao.Query;
import com.elepy.models.Model;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class MockCrudResource implements Crud<Resource> {
    @Override
    public Page<Resource> search(Query query, PageSettings settings) {
        return null;
    }

    @Override
    public Optional<Resource> getById(Serializable id) {
        return Optional.empty();
    }

    @Override
    public List<Resource> searchInField(Field field, String qry) {
        return null;
    }

    @Override
    public void update(Resource item) {

    }

    @Override
    public void create(Resource item) {

    }

    @Override
    public List<Resource> getAll() {
        return null;
    }

    @Override
    public void deleteById(Serializable id) {

    }

    @Override
    public Model<Resource> getModel() {
        return null;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return null;
    }
}
