package com.elepy.dao;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.concepts.IdentityProvider;
import com.elepy.concepts.Resource;
import com.elepy.dao.jongo.MongoDao;
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
    public Class<Resource> modelClassType() {
        return Resource.class;
    }

    @Override
    public String mongoCollectionName() {
        return "resources";
    }

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    public DB db() {
        return db;
    }

    @Override
    public IdentityProvider<Resource> identityProvider() {
        return null;
    }
}
