package com.elepy.dao.jongo;


import com.elepy.id.IdentityProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Jongo;

public class DefaultMongoDao<T> extends MongoDao<T> {

    private final DB db;
    private final Class<T> classType;
    private final String collectionName;
    private final IdentityProvider<T> identityProvider;
    private final ObjectMapper objectMapper;
    private final Jongo jongo;


    public DefaultMongoDao(final DB db, final String collectionName, final Class<T> classType) {
        this(db, collectionName, classType, null);
    }

    public DefaultMongoDao(final DB db, final String collectionName, final Class<T> classType, IdentityProvider<T> identityProvider) {
        this(db, collectionName, classType, identityProvider, new ObjectMapper());
    }

    public DefaultMongoDao(final DB db, final String collectionName, final Class<T> classType, IdentityProvider<T> identityProvider, ObjectMapper objectMapper) {
        this.db = db;
        this.classType = classType;
        this.collectionName = collectionName.replaceAll("/", "");
        this.identityProvider = identityProvider;
        this.objectMapper = objectMapper;
        this.jongo = new Jongo(db());

    }

    @Override
    Jongo getJongo() {
        return jongo;
    }

    @Override
    public Class<T> modelClassType() {
        return classType;
    }

    @Override
    public String mongoCollectionName() {
        return collectionName;
    }

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    public DB db() {
        return db;
    }

}
