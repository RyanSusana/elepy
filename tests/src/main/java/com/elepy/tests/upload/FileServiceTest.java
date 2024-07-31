package com.elepy.tests.upload;

import com.elepy.configuration.Configuration;
import com.elepy.Elepy;
import com.elepy.auth.permissions.DefaultPermissions;
import com.elepy.dao.Crud;
import com.elepy.dao.querymodel.Filters;
import com.elepy.tests.basic.Resource;
import com.elepy.uploads.FileReference;
import com.elepy.uploads.FileService;
import com.elepy.http.RawFile;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class FileServiceTest {

    private static int portCounter = 8700;
    private final Configuration[] configurations;
    protected final int port;

    protected Elepy elepy;
    protected FileService fileService;
    protected String url;

    private final Tika tika = new Tika();

    public FileServiceTest(Configuration... configurations) {
        this.configurations = configurations;

        port = ++portCounter;
    }

    public Configuration databaseConfiguration() {
        throw new UnsupportedOperationException("You need to implement this method");
    }

    public FileService fileService() {
        throw new UnsupportedOperationException("You need to implement this method");
    }


    @BeforeAll
    public void setUp() {
        this.fileService = fileService();
        this.url = String.format("http://localhost:%d", port);
        elepy = new Elepy()
                .addModel(Resource.class)
                .addConfiguration(databaseConfiguration())
                .withFileService(this.fileService)
                .withPort(port);

        List.of(configurations).forEach(elepy::addConfiguration);

        elepy.http().before(ctx -> ctx.request().addPermissions(DefaultPermissions.AUTHENTICATED, "files.*"));
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
    void can_UploadSameFile_MoreThanOnce() throws UnirestException, IOException {
        testCanUploadAndRead("double.txt", "text/*");

        assertDoesNotThrow(() -> testCanUploadAndRead("double.txt", "text/*"));
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

    private RawFile testCanUploadAndRead(String originalFileName, String contentType) throws UnirestException, IOException {
        final int fileCountBeforeUpload = countFiles();

        final Crud<FileReference> references = elepy.getCrudFor(FileReference.class);

        final HttpResponse<JsonNode> response = Unirest.post(url + "/elepy/uploads")
                .field("files", inputStream(originalFileName), ContentType.create(tika.detect(inputStream(originalFileName), originalFileName)), originalFileName)
                .asJson();

        assertThat(response.getStatus()).as(response.getBody().toString()).isEqualTo(200);

        final String uploadedFileName = response.getBody().getObject()
                .getJSONArray("files")
                .getJSONObject(0).getString("uploadName");

        final RawFile rawFile = fileService.readFile(uploadedFileName).orElseThrow(() ->
                new AssertionFailedError("FileService did not recognize file: " + uploadedFileName));

        final var s = uploadedFileName.split("_");

        final var byId = references.getById(uploadedFileName);
        final var foundReferences = references.findLimited(50, Filters.contains("uploadName", uploadedFileName));
        assertThat(foundReferences
                .stream().map(FileReference::getUploadName).anyMatch(uploadedFileName::equals))
                .as(String.format("Can't find '%s' in file references ", uploadedFileName))
                .isTrue();


        assertThat(inputStream(originalFileName).readAllBytes().length)
                .as("File  size of uploaded file not equal to the actual file")
                .isEqualTo(rawFile.getSize());

        assertThat(countFiles()).as("File upload did not increase the count of Files")
                .isEqualTo(fileCountBeforeUpload + 1);

        assertThat(rawFile.contentTypeMatches(contentType))
                .as(String.format("Content types don't match between the uploaded version and read version of '%s'. [Expected: %s, Actual: %s]", uploadedFileName, contentType, rawFile.getContentType()))
                .isTrue();


        assertThat(IOUtils.contentEquals(inputStream(originalFileName), rawFile.getContent())).as(String.format("Content doesn't match between the uploaded version and read version of '%s'", uploadedFileName)).isTrue();


        return rawFile;
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
