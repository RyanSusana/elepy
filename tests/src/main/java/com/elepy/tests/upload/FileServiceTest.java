package com.elepy.tests.upload;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.auth.Permissions;
import com.elepy.dao.Crud;
import com.elepy.tests.ElepyTest;
import com.elepy.tests.basic.Resource;
import com.elepy.uploads.FileReference;
import com.elepy.uploads.FileService;
import com.elepy.uploads.FileUpload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.tika.Tika;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class FileServiceTest implements ElepyTest {

    private static int portCounter = 8700;
    private final Configuration[] configurations;
    private final int port;

    private Elepy elepy;
    private FileService fileService;
    private String url;

    private Tika tika = new Tika();

    public FileServiceTest(Configuration... configurations) {
        this.configurations = configurations;

        port = ++portCounter;
    }

    public abstract Configuration databaseConfiguration();

    public abstract FileService fileService();


    @BeforeAll
    public void setUp() {
        this.fileService = fileService();
        url = String.format("http://localhost:%d", port);
        elepy = new Elepy()
                .addModel(Resource.class)
                .addConfiguration(databaseConfiguration())
                .withFileService(this.fileService)
                .onPort(port);

        List.of(configurations).forEach(elepy::addConfiguration);

        elepy.http().before(ctx -> ctx.request().addPermissions(Permissions.LOGGED_IN));
        elepy.start();
    }

    @AfterEach
    public void tearDown() {
        clearFileServiceFiles();
    }

    @AfterAll
    public void tearDownAll() {
        elepy.stop();
    }

    @Test
    void canNot_UploadSameFile_MoreThanOnce() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("double.txt", "text/*");

        final var assertionFailedError = assertThrows(AssertionFailedError.class, () ->
                testCanUploadAndRead("double.txt", "text/*"));

        assertEquals(409, assertionFailedError.getActual().getValue(),
                "The returned status code by duplicates MUST be 409!");
    }

    @Test
    void can_UploadAndRead_TextFile() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("uploadExampleText.txt", "text/plain");
    }

    @Test
    void can_UploadAndRead_GIF() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("LoadingWhite.gif", "image/gif");
    }

    @Test
    void can_UploadAndRead_PNG() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("logo-light.png", "image/png");
    }

    @Test
    void can_UploadAndRead_SVG() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("logo-dark.svg", "image/svg");
    }

    @Test
    void can_UploadAndRead_JPEG() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("doggo.jpg", "image/jpeg");
    }

    @Test
    void can_UploadAndRead_PDF() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("cv.pdf", "application/pdf");
    }

    @Test
    void can_UploadAndRead_MP4() throws UnirestException, IOException, InterruptedException {
        testCanUploadAndRead("nature.mp4", "video/mp4");
    }

    private FileUpload testCanUploadAndRead(String originalFileName, String contentType) throws UnirestException, IOException {
        final int fileCountBeforeUpload = countFiles();

        final Crud<FileReference> references = elepy.getCrudFor(FileReference.class);

        final HttpResponse<JsonNode> response = Unirest.post(url + "/uploads")
                .field("files", inputStream(originalFileName), ContentType.create(tika.detect(inputStream(originalFileName), originalFileName)), originalFileName)
                .asJson();

        assertEquals(200, response.getStatus(), response.getBody().toString());

        final String uploadedFileName = response.getBody().getObject()
                .getJSONArray("files")
                .getJSONObject(0).getString("uploadName");

        final FileUpload fileUpload = fileService.readFile(uploadedFileName).orElseThrow(() ->
                new AssertionFailedError("FileService did not recognize file: " + uploadedFileName));


        assertTrue(references.searchInField("uploadName", uploadedFileName)
                        .stream().map(FileReference::getUploadName).anyMatch(uploadedFileName::equals),
                String.format("Can't find '%s' in file references ", uploadedFileName)
        );
        assertEquals(fileUpload.getSize(), inputStream(originalFileName).readAllBytes().length, "File  size of uploaded file not equal to the actual file");

        assertEquals(fileCountBeforeUpload + 1, countFiles(),
                "File upload did not increase the count of Files");
        assertTrue(fileUpload.contentTypeMatches(contentType),
                String.format("Content types don't match between the uploaded version and read version of '%s'. [Expected: %s, Actual: %s]", uploadedFileName, contentType, fileUpload.getContentType()));


        assertTrue(IOUtils.contentEquals(inputStream(originalFileName), fileUpload.getContent()),
                String.format("Content doesn't match between the uploaded version and read version of '%s'", uploadedFileName));

        return fileUpload;
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
