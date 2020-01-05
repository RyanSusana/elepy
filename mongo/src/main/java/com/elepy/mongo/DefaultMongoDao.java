package com.elepy.mongo;


import com.elepy.models.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Jongo;

public class DefaultMongoDao<T> extends MongoDao<T> {

    private final DB db;
    private final Schema<T> schema;
    private final String collectionName;
    private final ObjectMapper objectMapper;
    private final Jongo jongo;


    public DefaultMongoDao(final DB db, final String collectionName, final Schema<T> schema) {
        this(db, collectionName, schema, new ObjectMapper());
    }


    public DefaultMongoDao(final DB db, final String collectionName, final Schema<T> schema, ObjectMapper objectMapper) {
        this.db = db;
        this.schema = schema;
        this.collectionName = collectionName.replaceAll("/", "");
        this.objectMapper = objectMapper;

        this.jongo = new Jongo(db(), JongoMapperFactory.createMapper());

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
    public Schema<T> getSchema() {
        return schema;
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
