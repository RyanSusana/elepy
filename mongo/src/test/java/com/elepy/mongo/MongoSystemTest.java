package com.elepy.mongo;

import com.elepy.Elepy;
import com.elepy.tests.SystemTest;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.jupiter.api.AfterEach;

import java.net.InetSocketAddress;

public class MongoSystemTest extends SystemTest {

    private MongoServer mongoServer;

    @Override
    public void configureElepy(Elepy elepy) {

        mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));
        elepy.addConfiguration(MongoConfiguration.of(client, "test", "bucket"));
    }

    @Override
    @AfterEach
    public void tearDown() {
        super.tearDown();
        mongoServer.shutdownNow();
    }
}
