package com.elepy.tests.upload;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.tests.ElepyTest;
import com.elepy.tests.basic.Resource;
import com.elepy.uploads.FileService;
import com.github.fakemongo.Fongo;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class UploadEndToEndTest implements ElepyTest {

    private static int portCounter = 7900;

    private final Elepy elepy;
    private final FileService fileService;
    private final String url;

    public UploadEndToEndTest(Configuration... configurations) {
        final int port = ++portCounter;
        this.fileService = fileService();
        url = String.format("http://localhost:%d", port);
        elepy = new Elepy()
                .addModel(Resource.class)
                .addConfiguration(MongoConfiguration.of(new Fongo("test").getDB("test")))
                .withUploads(this.fileService)
                .onPort(port);

        List.of(configurations).forEach(elepy::addConfiguration);
        elepy.start();
    }

    public abstract FileService fileService();


    @AfterAll
    void tearDownAll() {
        elepy.stop();
    }

    @Test
    void testUploadViaHTTP() throws UnirestException {

        final HttpResponse<String> response = Unirest.post(url + "/uploads")
                .field("files", new File("src/test/resources/textFileToUpload.txt")).asString();


        final List<String> collect = fileService.listFiles();

        assertEquals(200, response.getStatus());
        assertEquals(1, collect.size());
    }


    @Test
    void testRetrieveViaHTTP() {

    }
}
