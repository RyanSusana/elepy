package com.elepy.mongo;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.models.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

public class MongoCrudFactory implements CrudFactory {

    @Inject
    private DB database;

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public <T> Crud<T> crudFor(Model<T> model) {

        final String slug = model.getSlug();

        final String[] split = slug.split("/");

        return new DefaultMongoDao<>(database, split[split.length - 1], model, objectMapper);
    }
}
