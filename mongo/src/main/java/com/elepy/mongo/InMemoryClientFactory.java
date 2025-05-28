package com.elepy.mongo;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

public class InMemoryClientFactory {
    static final Logger logger = org.slf4j.LoggerFactory.getLogger(InMemoryClientFactory.class);


    private InMemoryClientFactory() {
    }


    public static MongoClient createInMemoryClient() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();
        logger.info("Mongo started on {}", serverAddress);
        return new MongoClient(new ServerAddress(serverAddress));

    }
} 
