package com.elepy.mongo.fast;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BaseFongo extends Base {


    private MongoClient _mongo;

    private MongoClient client;
    private int port;

    private MongoServer mongoServer;

    public void setUp() throws Exception {
        mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        client = new MongoClient(new ServerAddress(serverAddress));
    }

    @AfterAll
    void tearDown() {
        mongoServer.shutdownNow();
    }

    public DB getDb() throws IOException {
        return client.getDB("test");
    }
}
