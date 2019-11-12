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
import static org.assertj.core.api.Assertions.assertThat;


public class UploadTest {
    private static final String UPLOAD_DIR = "src/test/resources/uploads";
    private DirectoryFileService directoryFileService;

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

        assertThat(Files.exists(pathToBeDeleted)).as("Directory still exists").isFalse();
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        directoryFileService = new DirectoryFileService(UPLOAD_DIR);

        directoryFileService.ensureRootFolderExists();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteDir();

    }

    @Test
    void testUploadText() throws IOException {

        String filePath = "src/test/resources/textFileToUpload.txt";


        final FileUpload text = FileUpload.of("textFileToUpload.txt", "text", Files.newInputStream(Paths.get(filePath)), Files.size(Paths.get(filePath)));

        directoryFileService.uploadFile(text);

        final List<Path> collect = Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList());


        assertThat(collect.size()).isEqualTo(1);

        List<String> lines = Files.readAllLines(collect.get(0), UTF_8);


        assertThat(lines.get(0)).isEqualTo("TEST");
        assertThat(collect.get(0).getFileName().toString()).isEqualTo("textFileToUpload.txt");
    }

    @Test
    void testReadText() throws IOException {
        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));

        final FileUpload fileUpload = directoryFileService.readFile("textFileToUpload.txt").orElse(null);
        assertThat(fileUpload).isNotNull();

        assertThat(fileUpload.getName()).isEqualTo("textFileToUpload.txt");
        assertThat(IOUtils.toString(fileUpload.getContent())).isEqualTo("TEST");
        assertThat(fileUpload.getContentType()).isEqualTo("text/plain");
    }

    @Test
    void testListFiles() throws IOException {

        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));

        final List<String> list = directoryFileService.listFiles();
        assertThat(list.size()).isEqualTo(1);

        assertThat(list.get(0)).isEqualTo("textFileToUpload.txt");
    }

    @Test
    void testDeleteFile() throws IOException {
        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));

        directoryFileService.deleteFile("textFileToUpload.txt");
        assertThat(Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList()).size()).isEqualTo(0);
    }

    @Test
    void testReadFileNonExistentThrows04() throws IOException {
        Files.copy(Paths.get("src/test/resources/textFileToUpload.txt"), Paths.get(UPLOAD_DIR + "/textFileToUpload.txt"));
        final Optional<FileUpload> uploadedFile = directoryFileService.readFile("nonExistent.txt");

        assertThat(uploadedFile.isEmpty()).isTrue();
        assertThat(Files.list(Paths.get(UPLOAD_DIR)).collect(Collectors.toList()).size()).isEqualTo(1);

    }
}
