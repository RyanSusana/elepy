package com.elepy.mongo;

import com.elepy.Configuration;
import com.elepy.tests.basic.BasicFuntionalityTest;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.jupiter.api.AfterAll;

import java.net.InetSocketAddress;

public class MongoFunctionalityTest extends BasicFuntionalityTest {

    private MongoServer mongoServer;

    @Override
    public Configuration configuration() {
        mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));
        return MongoConfiguration.of(client, "test");
    }

    @Override
    @AfterAll
    protected void tearDownAll() {
        super.tearDownAll();
        mongoServer.shutdownNow();
    }
}
