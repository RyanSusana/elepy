package com.elepy.uploads;

import org.junit.jupiter.api.*;
import spark.utils.IOUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;


public class UploadTest {
    private static final String UPLOAD_DIR = "src/test/resources/uploads";
    private DirectoryFileService directoryFileService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        directoryFileService = new DirectoryFileService(UPLOAD_DIR);

        directoryFileService.ensureDirsMade();
    }

    @BeforeAll
    public static void clear() {

        try {
            deleteDir();
        } catch (IOException ignored) {

        }

    }

    private static void deleteDir() throws IOException {
        Path pathToBeDeleted = Paths.get(UPLOAD_DIR);

        Files.walkFileTree(pathToBeDeleted,
                new SimpleFileVisitor<>() {
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

        Assertions.assertFalse(
                Files.exists(pathToBeDeleted), "Directory still exists");
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteDir();

    }
    @Test
    void testUploadText() throws IOException {

        String filePath = "src/test/resources/textFileToUpload.txt";


        final UploadedFile text = UploadedFile.of("text", Files.newInputStream(Paths.get(filePath)), "textFileToUpload.txt");

        directoryFileService.uploadFile(text);

        final List<Path> collect = Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList());


        Assertions.assertEquals(1, collect.size());

        List<String> lines = Files.readAllLines(collect.get(0), UTF_8);


        Assertions.assertEquals("TEST", lines.get(0));
        Assertions.assertEquals("textFileToUpload.txt", collect.get(0).getFileName().toString());
    }

    @Test
    void testReadText() throws IOException {
        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));

        final UploadedFile uploadedFile = directoryFileService.readFile("textFileToUpload.txt").orElse(null);
        assertNotNull(uploadedFile);

        assertEquals("textFileToUpload.txt", uploadedFile.getName());
        assertEquals("TEST", IOUtils.toString(uploadedFile.getContent()));
        assertEquals("text/plain", uploadedFile.getContentType());
    }

    @Test
    void testListFiles() throws IOException {

        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));

        final List<String> list = directoryFileService.listFiles();
        assertEquals(1, list.size());

        assertEquals("textFileToUpload.txt", list.get(0));
    }

    @Test
    void testDeleteFile() throws IOException {
        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));

        directoryFileService.deleteFile("textFileToUpload.txt");
        assertEquals(0, Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList()).size());
    }

    @Test
    void testReadFileNonExistentThrows04() throws IOException {
        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));
        final Optional<UploadedFile> uploadedFile = directoryFileService.readFile("nonExistent.txt");

        assertTrue(uploadedFile.isEmpty());
        assertEquals(1, Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList()).size());

    }
}
