package com.elepy.mongo;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Property;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConfiguration implements Configuration {

    private final MongoClient mongoClient;

    private final String databaseName;

    private final String bucket;

    @ElepyConstructor
    public MongoConfiguration(
            @Property(key = "mongo.username") String username,
            @Property(key = "mongo.password") String password,
            @Property(key = "mongo.host") String server,
            @Property(key = "mongo.databaseName") String databaseName,
            @Property(key = "mongo.bucket") String bucket,
            @Property(key = "mongo.memory") boolean inMemory
    ) {

        if (inMemory) {
            this.databaseName = "in-memory";
            this.bucket = "in-memory-bucket";
            this.mongoClient = InMemoryClientFactory.createInMemoryClient();
        } else {
            MongoClientURI uri = new MongoClientURI(String.format("mongodb+srv://%s:%s@%s", username, password, server));
            this.mongoClient = new MongoClient(uri);

            this.databaseName = databaseName;
            this.bucket = bucket;
        }

    }

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
        return inMemory("in-memory-database", "in-memory-fileservice");
    }

    public static MongoConfiguration inMemory(String database, String bucket) {
        return of(InMemoryClientFactory.createInMemoryClient(), database, bucket);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        if (databaseName != null) {
            elepy.registerDependency(MongoDatabase.class, mongoClient.getDatabase(databaseName));
            elepy.withDefaultCrudFactory(MongoCrudFactory.class);
        }

        if (bucket != null) {
            elepy.withUploads(new MongoFileService(mongoClient.getDatabase(databaseName), null));
        }
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
