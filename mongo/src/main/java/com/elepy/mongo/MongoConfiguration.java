package com.elepy.mongo;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoConfiguration implements Configuration {

    private final DB db;

    public MongoConfiguration(MongoClient mongoClient, String databaseName) {
        this(mongoClient.getDB(databaseName));
    }

    public MongoConfiguration(DB db) {
        this.db = db;
    }

    public static MongoConfiguration of(MongoClient mongoClient, String database) {
        return new MongoConfiguration(mongoClient, database);
    }

    public static MongoConfiguration of(DB db) {
        return new MongoConfiguration(db);
    }

    @Override
    public void before(ElepyPreConfiguration elepy) {
        elepy.registerDependency(DB.class, db);
        elepy.withDefaultCrudProvider(MongoCrudFactory.class);
    }

    @Override
    public void after(ElepyPostConfiguration elepy) {

    }
}
