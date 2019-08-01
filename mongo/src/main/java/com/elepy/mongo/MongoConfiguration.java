package com.elepy.mongo;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoConfiguration implements Configuration {

    private final MongoClient mongoClient;

    private final String databaseName;

    private final String bucket;

    public MongoConfiguration(MongoClient mongoClient, String databaseName, String bucket) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        this.bucket = bucket;
    }

    public static MongoConfiguration of(MongoClient mongoClient, String database) {
        return new MongoConfiguration(mongoClient, database, null);
    }

    public static MongoConfiguration of(MongoClient mongoClient, String database, String bucket) {
        return new MongoConfiguration(mongoClient, database, bucket);
    }

    public static MongoConfiguration inMemory() {

        return of(InMemoryClientFactory.createInMemoryClient(), "in-memory-database", "in-memory-fileservice");
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.registerDependency(DB.class, mongoClient.getDB(databaseName));
        elepy.withDefaultCrudFactory(MongoCrudFactory.class);

        if (bucket != null) {
            elepy.withUploads(new MongoFileService(mongoClient.getDatabase(databaseName), null));
        }
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
