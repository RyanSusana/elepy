package com.elepy.tests.devfrontend;

import com.elepy.Elepy;
import com.elepy.admin.FrontendLoader;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.sparkjava.SparkService;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));

        final var elepyInstance = new Elepy()
                .addConfiguration(MongoConfiguration.of(client, "example", "bucket"))
                .withPort(7331)
                .addModelPackage("com.elepy.tests.devfrontend")
                .addLocale(new Locale("nl"), "Nederlands")
                .withHttpService(SparkService.class)
//                .addConfiguration(OAuthConfiguration.of(
//                        new GoogleAuthScheme(env("GOOGLE_KEY"), env("GOOGLE_SECRET")),
//                        new GitHubAuthScheme(env("GH_KEY"), env("GH_SECRET")),
//                        new MicrosoftADAuthScheme(env("MS_TENANT"), env("MS_KEY"), env("MS_SECRET")),
//                        new FacebookAuthScheme(env("FB_KEY"), env("FB_SECRET")))
//                )

                .addExtension(FrontendLoader.class);

        elepyInstance.start();


        Thread.sleep(1000);


    }

    private static String env(String s) {
        return System.getenv(s);
    }
} 
