package com.elepy.mongo.fast;

import com.elepy.Elepy;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.tests.basic.BasicFunctionalityTest;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.jupiter.api.AfterAll;

import java.net.InetSocketAddress;

public class MongoFunctionalityTest extends BasicFunctionalityTest {

    private MongoServer mongoServer;

    @Override
    @AfterAll
    protected void tearDownAll() {
        super.tearDownAll();
        mongoServer.shutdownNow();
    }

    @Override
    public void configureElepy(Elepy elepy) {
        mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));

        elepy.addConfiguration(MongoConfiguration.of(client, "test", "bucket"));
    }
}
