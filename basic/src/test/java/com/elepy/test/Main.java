package com.elepy.test;

import com.elepy.Elepy;
import com.elepy.auth.Permissions;
import com.elepy.mongo.MongoConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));

        new Elepy()
                .addConfiguration(MongoConfiguration.of(client, "example", "bucket"))
                .addExtension((http, elepy) -> http.before(context -> context.request().addPermissions(Permissions.SUPER_USER)))
                .addModel(Product.class)
                .start();
    }
} 
