package com.elepy.mongo;


import com.elepy.describers.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Jongo;

public class DefaultMongoDao<T> extends MongoDao<T> {

    private final DB db;
    private final Model<T> model;
    private final String collectionName;
    private final ObjectMapper objectMapper;
    private final Jongo jongo;


    public DefaultMongoDao(final DB db, final String collectionName, final Model<T> model) {
        this(db, collectionName, model, new ObjectMapper());
    }


    public DefaultMongoDao(final DB db, final String collectionName, final Model<T> model, ObjectMapper objectMapper) {
        this.db = db;
        this.model = model;
        this.collectionName = collectionName.replaceAll("/", "");
        this.objectMapper = objectMapper;
        this.jongo = new Jongo(db(), new ElepyMapper(this));

    }

    @Override
    Jongo getJongo() {
        return jongo;
    }


    @Override
    public String mongoCollectionName() {
        return collectionName;
    }

    @Override
    public Model<T> getModel() {
        return model;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public DB db() {
        return db;
    }

}
