package com.elepy.tests.upload;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.tests.ElepyTest;
import com.elepy.tests.basic.Resource;
import com.elepy.uploads.FileService;
import com.elepy.uploads.UploadedFile;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class FileServiceTest implements ElepyTest {

    private static int portCounter = 7900;

    private final Elepy elepy;
    private final FileService fileService;
    private final String url;

    public FileServiceTest(Configuration... configurations) {
        final int port = ++portCounter;
        this.fileService = fileService();
        url = String.format("http://localhost:%d", port);
        elepy = new Elepy()
                .addModel(Resource.class)
                .addConfiguration(databaseConfiguration())
                .withUploads(this.fileService)
                .onPort(port);

        List.of(configurations).forEach(elepy::addConfiguration);
        elepy.start();
    }

    public abstract Configuration databaseConfiguration();
    public abstract FileService fileService();


    @BeforeAll
    void setUp() {
        clearFileServiceFiles();
    }

    @AfterEach
    void tearDown() {
        clearFileServiceFiles();
    }

    @AfterAll
    void tearDownAll() {
        elepy.stop();
    }

    @Test
    void canNot_UploadSameFile_MoreThanOnce() throws UnirestException, IOException {
        testCanUploadAndRead("double.txt", "text/*");

        final var assertionFailedError = assertThrows(AssertionFailedError.class, () ->
                testCanUploadAndRead("double.txt", "text/*"));

        assertEquals(409, assertionFailedError.getActual().getValue(),
                "The returned status code by duplicates MUST be 409!");
    }

    @Test
    void can_UploadAndRead_TextFile() throws UnirestException, IOException {
        testCanUploadAndRead("uploadExampleText.txt", "text/plain");
    }

    @Test
    void can_UploadAndRead_GIF() throws UnirestException, IOException {
        testCanUploadAndRead("LoadingWhite.gif", "image/gif");
    }

    @Test
    void can_UploadAndRead_PNG() throws UnirestException, IOException {
        testCanUploadAndRead("logo-light.png", "image/png");
    }

    @Test
    void can_UploadAndRead_SVG() throws UnirestException, IOException {
        testCanUploadAndRead("logo-dark.svg", "image/svg");
    }

    @Test
    void can_UploadAndRead_JPEG() throws UnirestException, IOException {
        testCanUploadAndRead("doggo.jpg", "image/jpeg");
    }

    @Test
    void can_UploadAndRead_PDF() throws UnirestException, IOException {
        testCanUploadAndRead("cv.pdf", "application/pdf");
    }

    @Test
    void can_UploadAndRead_MP4() throws UnirestException, IOException {
        testCanUploadAndRead("nature.mp4", "video/mp4");
    }

    private UploadedFile testCanUploadAndRead(String fileName, String contentType) throws UnirestException, IOException {


        final int fileCountBeforeUpload = countFiles();
        final InputStream resourceAsStream = inputStream(fileName);
        final HttpResponse<String> response = Unirest.post(url + "/uploads")
                .field("files", resourceAsStream, fileName).asString();


        final UploadedFile uploadedFile = fileService.readFile(fileName).orElseThrow(() ->
                new AssertionFailedError("FileService did not recognize file: " + fileName));

        assertEquals(200, response.getStatus(), response.getBody());
        assertEquals(fileCountBeforeUpload + 1, countFiles(),
                "File upload did not increase the count of Files");
        assertTrue(uploadedFile.contentTypeEquals(contentType),
                String.format("Content types don't match between the uploaded version and read version of '%s'. [Expected: %s, Actual: %s]", fileName, contentType, uploadedFile.getContentType()));
        assertTrue(IOUtils.contentEquals(inputStream(fileName), uploadedFile.getContent()),
                String.format("Content doesn't match between the uploaded version and read version of '%s'", fileName));

        return uploadedFile;
    }


    private InputStream inputStream(String name) {
        return Optional.ofNullable(this.getClass().getResourceAsStream("/" + name))
                .orElseThrow(() -> new AssertionFailedError(String.format("The file '%s' can't be found in resources", name)));
    }

    private int countFiles() {
        return fileService.listFiles().size();
    }

    private void clearFileServiceFiles() {
        fileService.listFiles().forEach(fileService::deleteFile);
    }

}
