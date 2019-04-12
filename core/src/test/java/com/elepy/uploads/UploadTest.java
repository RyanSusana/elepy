package com.elepy.uploads;

import com.elepy.http.UploadedFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class UploadTest {
    private static final String UPLOAD_DIR = "src/test/resources/uploads";
    private DirectoryFileService directoryFileService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {


        directoryFileService = new DirectoryFileService(UPLOAD_DIR);

        directoryFileService.ensureDirsMade();
    }

    @AfterEach
    void tearDown() throws IOException {

        Path pathToBeDeleted = Paths.get("src/test/resources/uploads");

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

        assertFalse(
                Files.exists(pathToBeDeleted), "Directory still exists");
    }

    @Test
    void testUploadText() throws IOException {

        String filePath = "src/test/resources/textFileToUpload.txt";


        final UploadedFile text = UploadedFile.of("text", Files.newInputStream(Paths.get(filePath)), "textFileToUpload.txt", ".txt");

        directoryFileService.uploadFile(text);

        final List<Path> collect = Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList());


        assertEquals(1, collect.size());

        List<String> lines = Files.readAllLines(collect.get(0), UTF_8);


        assertEquals("TEST", lines.get(0));
        assertEquals("textFileToUpload.txt", collect.get(0).getFileName().toString());
    }
}
