package com.elepy.tests.upload;

import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.auth.users.User;
import com.elepy.auth.users.UserService;
import com.elepy.configuration.Configuration;
import com.elepy.Elepy;
import com.elepy.crud.Crud;
import com.elepy.query.Filters;
import com.elepy.tests.basic.Resource;
import com.elepy.uploads.FileReference;
import com.elepy.uploads.FileService;
import com.elepy.http.RawFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final ObjectMapper objectMapper = new ObjectMapper();
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
        elepy.start();
        var dependency = elepy.getDependency(UserService.class);
        dependency.createUser(null, new User("admin", "admin", "admin"));
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
    void can_UploadSameFile_MoreThanOnce() throws  IOException, URISyntaxException, InterruptedException {
        testCanUploadAndRead("double.txt", "text/*");

        assertDoesNotThrow(() -> testCanUploadAndRead("double.txt", "text/*"));
    }

    @Test
    void can_UploadAndRead_TextFile() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("uploadExampleText.txt", "text/plain");
    }

    @Test
    void can_UploadAndRead_GIF() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("LoadingWhite.gif", "image/gif");
    }

    @Test
    void can_UploadAndRead_PNG() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("logo-light.png", "image/png");
    }

    @Test
    void can_UploadAndRead_SVG() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("logo-dark.svg", "image/svg");
    }

    @Test
    void can_UploadAndRead_JPEG() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("doggo.jpg", "image/jpeg");
    }

    @Test
    void can_UploadAndRead_PDF() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("cv.pdf", "application/pdf");
    }

    @Test
    void can_UploadAndRead_MP4() throws  IOException, InterruptedException, URISyntaxException {
        testCanUploadAndRead("nature.mp4", "video/mp4");
    }

    private RawFile testCanUploadAndRead(String originalFileName, String contentType) throws IOException, InterruptedException, URISyntaxException {
        final int fileCountBeforeUpload = countFiles();

        final Crud<FileReference> references = elepy.getCrudFor(FileReference.class);

        // Prepare the file for upload
        Path originalFilePath = Paths.get(getClass().getClassLoader().getResource(originalFileName).toURI());
        byte[] fileBytes = Files.readAllBytes(originalFilePath);

        // Create a unique boundary for the multipart request
        String boundary = UUID.randomUUID().toString();

        // Build the HTTP request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/elepy/uploads"))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes(StandardCharsets.UTF_8)))
                .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> createMultipartBody(originalFileName, contentType, fileBytes, boundary)))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);

        // Parse the JSON response
        var jsonResponse = objectMapper.readTree(response.body());
        var filesNode = jsonResponse.get("files");
        final String uploadedFileName =
                jsonResponse.get("files").get(0).get("uploadName").textValue();

        final RawFile rawFile = fileService.readFile(uploadedFileName).orElseThrow(() ->
                new AssertionFailedError("FileService did not recognize file: " + uploadedFileName));

        // The rest of your assertions remain largely the same
        final var s = uploadedFileName.split("_");

        final var byId = references.getById(uploadedFileName);
        final var foundReferences = references.findLimited(50, Filters.contains("uploadName", uploadedFileName));
        assertThat(foundReferences
                .stream().map(FileReference::getUploadName).anyMatch(uploadedFileName::equals))
                .as(String.format("Can't find '%s' in file references ", uploadedFileName))
                .isTrue();


        assertThat(fileBytes.length) // Use the directly read fileBytes.length
                .as("File size of uploaded file not equal to the actual file")
                .isEqualTo(rawFile.getSize());

        assertThat(countFiles()).as("File upload did not increase the count of Files")
                .isEqualTo(fileCountBeforeUpload + 1);

        assertThat(rawFile.contentTypeMatches(contentType))
                .as(String.format("Content types don't match between the uploaded version and read version of '%s'. [Expected: %s, Actual: %s]", uploadedFileName, contentType, rawFile.getContentType()))
                .isTrue();


        assertThat(IOUtils.contentEquals(inputStream(originalFileName), rawFile.getContent())).as(String.format("Content doesn't match between the uploaded version and read version of '%s'", uploadedFileName)).isTrue();


        return rawFile;
    }

    private InputStream createMultipartBody(String originalFileName, String contentType, byte[] fileBytes, String boundary) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            // Part for the file
            os.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            os.write(("Content-Disposition: form-data; name=\"files\"; filename=\"" + originalFileName + "\"\r\n").getBytes(StandardCharsets.UTF_8));
            os.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            os.write(fileBytes);
            os.write(("\r\n").getBytes(StandardCharsets.UTF_8));

            // End boundary
            os.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create multipart body", e);
        }
        return new ByteArrayInputStream(os.toByteArray());
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
