package com.elepy.uploads;

import com.elepy.http.SparkService;
import com.elepy.igniters.UploadIgniter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElepyEndToEndTest {

    private static final String UPLOAD_DIR = "src/test/resources/uploads";
    private SparkService sparkService;

    @BeforeEach
    void setUp() {
        final Service port = Service.ignite().port(1335);

        port.init();
        port.awaitInitialization();
        sparkService = new SparkService(port, null);

        sparkService.ignite();

        port.awaitInitialization();
        final UploadIgniter uploadIgniter = new UploadIgniter(sparkService, new ObjectMapper(), new DirectoryFileService(UPLOAD_DIR));

        uploadIgniter.ignite();
    }

    @AfterEach
    void tearDown() throws IOException {

        sparkService.stop();
        Path pathToBeDeleted = Paths.get(UPLOAD_DIR);

        if (pathToBeDeleted.toFile().exists()) {
            Files.walkFileTree(pathToBeDeleted,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(
                                Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });

        }
        Assertions.assertFalse(
                Files.exists(pathToBeDeleted), "Directory still exists");
    }

    @Test
    void testUploadViaHTTP() throws UnirestException, IOException {

        final HttpResponse<String> response = Unirest.post("http://localhost:1335/uploads")
                .field("files", new File("src/test/resources/textFileToUpload.txt")).asString();

        assertEquals(200, response.getStatus());
        final List<Path> collect = Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList());

        assertEquals(1, collect.size());

        List<String> lines = Files.readAllLines(collect.get(0), UTF_8);

        Assertions.assertEquals("TEST", lines.get(0));

    }


    @Test
    void testRetrieveViaHTTP() {

    }
}
