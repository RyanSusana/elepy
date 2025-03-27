package com.elepy.mongo;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;

public class InMemoryClientFactory {


    private InMemoryClientFactory() {
    }


    public static MongoClient createInMemoryClient() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();
        System.out.println("Mongo started on " + serverAddress);
        return new MongoClient(new ServerAddress(serverAddress));

    }
} 
