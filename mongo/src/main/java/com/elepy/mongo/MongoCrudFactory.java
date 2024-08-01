package com.elepy.mongo;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.schemas.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import jakarta.inject.Inject;

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
