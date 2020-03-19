package com.elepy.tests.compatibility;

import com.elepy.Elepy;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.tests.ElepyConfigHelper;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;

public class MongoStack implements ElepyConfigHelper {
    private MongoServer mongoServer;

    @Override
    public void configureElepy(Elepy elepy) {

        mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        MongoClient client = new MongoClient(new ServerAddress(serverAddress));
        elepy.addConfiguration(MongoConfiguration.of(client, "test", "bucket"));
    }

    @Override
    public void teardown() {
        mongoServer.shutdownNow();
    }
}
