package com.elepy.mongo;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.models.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;

public class MongoCrudFactory implements CrudFactory {

    @Inject
    private MongoDatabase database;

    @Inject
    private ObjectMapper objectMapper;


    @Override
    public <T> Crud<T> crudFor(Schema<T> schema) {

        final String path = schema.getPath();
        final String[] split = path.split("/");


        return new MongoDao<>(database, split[split.length - 1], schema);
    }
}
