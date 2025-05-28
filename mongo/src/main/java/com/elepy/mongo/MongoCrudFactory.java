package com.elepy.mongo;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.schemas.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoCrudFactory implements CrudFactory {

    private MongoDatabase database;

    @Inject
    public MongoCrudFactory(MongoDatabase database) {
        this.database = database;
    }


    @Override
    public <T> Crud<T> crudFor(Schema<T> schema) {

        final String path = schema.getPath();
        final String[] split = path.split("/");


        return new MongoCrud<>(database, split[split.length - 1], schema);
    }
}
