package com.elepy.mongo.fast;

import com.elepy.configuration.Configuration;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.mongo.MongoFileService;
import com.elepy.tests.upload.FileServiceTest;
import com.elepy.uploads.FileService;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.net.InetSocketAddress;

public class MongoFileServiceTest extends FileServiceTest {
    private MongoClient client;

    private MongoServer mongoServer;

    @BeforeAll
    public void setUp() {
        mongoServer = new MongoServer(new MemoryBackend());

        InetSocketAddress serverAddress = mongoServer.bind();

        client = new MongoClient(new ServerAddress(serverAddress));
        super.setUp();

    }

    @AfterAll
    public void tearDown() {
        super.tearDown();
        mongoServer.shutdownNow();
    }

    @Override
    public Configuration databaseConfiguration() {
        return MongoConfiguration.of(client, "test");
    }

    @Override
    public FileService fileService() {
        return new MongoFileService(client.getDatabase("test"), null);

    }
}
