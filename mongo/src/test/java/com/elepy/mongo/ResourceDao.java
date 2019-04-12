package com.elepy.mongo;

import com.elepy.annotations.ElepyConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

public class ResourceDao extends MongoDao<Resource> {

    private final ObjectMapper objectMapper;
    private final DB db;

    @ElepyConstructor
    public ResourceDao(ObjectMapper objectMapper, DB db) {
        this.objectMapper = objectMapper;
        this.db = db;
    }

    @Override
    public Class<Resource> modelType() {
        return Resource.class;
    }

    @Override
    public String mongoCollectionName() {
        return "resources";
    }

    @Override
    public DB db() {
        return db;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
