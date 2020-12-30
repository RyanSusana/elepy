package com.elepy.tests.devfrontend;

import com.elepy.Elepy;
import com.elepy.admin.FrontendLoader;
import com.elepy.auth.Permissions;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Translated;
import com.elepy.mongo.MongoConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));

        final var elepyInstance = new Elepy()
                .addConfiguration(MongoConfiguration.of(client, "example", "bucket"))
                .withPort(7331)
                .addModelPackage("com.elepy.tests.devfrontend")
                .addExtension((http, elepy) -> {
                    http.get("/doggo", ctx -> {
                        final var dog = new Dog();
                        dog.setName("Doug, the Pug");
                        dog.setDescription("A very very very good fluffin boy");
                        dog.setNicknames(List.of("Roberto"));
                        ctx.validate(dog);
                    });
                    http.before(context -> {
                        context.response().header("Access-Control-Allow-Headers", "*");
                        context.request().addPermissions(Permissions.SUPER_USER);
                    });
                })
                .addExtension(new FrontendLoader());

        elepyInstance.alterModel(Post.class, modelContext -> modelContext.getSchema().setKeepRevisions(10));
        elepyInstance.start();

    }
} 
