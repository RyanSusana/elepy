package com.elepy.tests;

import com.elepy.Elepy;
import com.elepy.admin.FrontendLoader;
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
                .withPort(7331)
                .addModelPackage("com.elepy.tests")
                .addExtension((http, elepy) -> {
                    http.before(context -> {
                        context.response().header("Access-Control-Allow-Headers", "*");
                        context.request().addPermissions(Permissions.SUPER_USER);
                    });
                })
                .addExtension(new FrontendLoader())
                .addModel(Settings.class)
                .start();
    }
} 
