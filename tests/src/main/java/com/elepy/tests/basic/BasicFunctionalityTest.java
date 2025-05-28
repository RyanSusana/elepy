package com.elepy.tests.basic;

import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.auth.users.User;
import com.elepy.crud.Crud;
import com.elepy.query.SortOption;
import com.elepy.exceptions.Message;
import com.elepy.tests.CustomUser;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.elepy.query.Filters.search;
import static com.elepy.query.Queries.create;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
public abstract class BasicFunctionalityTest implements ElepyConfigHelper {

    private Crud<? extends User> userCrud;
    private Crud<? extends PolicyBinding> policyCrud;
    private Crud<Resource> resourceCrud;

    private static int resourceCounter = -100;
    protected ElepySystemUnderTest elepy;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeAll
    protected void setUpAll() {
        httpClient = HttpClient.newHttpClient(); // Initialize HttpClient once for all tests
        objectMapper = new ObjectMapper(); // Initialize ObjectMapper once for all tests

        elepy = ElepySystemUnderTest.create();

        elepy.addModel(Resource.class);
        elepy.addModel(CustomUser.class);
        this.configureElepy(elepy);

        elepy.start();

        resourceCrud = elepy.getCrudFor(Resource.class);
        userCrud = elepy.getCrudFor(User.class);
        policyCrud = elepy.getCrudFor(PolicyBinding.class);
    }

    @BeforeEach
    protected void setUp() {
        resourceCrud.delete(resourceCrud.getAll().stream().map(Resource::getId).collect(Collectors.toList()));
        userCrud.delete(userCrud.getAll().stream().map(User::getId).collect(Collectors.toList()));
    }

    @AfterAll
    protected void tearDownAll() {
        elepy.stop();
    }

    // Helper methods for HTTP requests using HttpClient
    private HttpResponse<String> sendGetRequest(String url, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(String url, String body, String contentType, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body, StandardCharsets.UTF_8));

        if (contentType != null && !contentType.isEmpty()) {
            requestBuilder.header("Content-Type", contentType);
        }
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPutRequest(String url, String body, String contentType, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(body == null ? "" : body, StandardCharsets.UTF_8));

        if (contentType != null && !contentType.isEmpty()) {
            requestBuilder.header("Content-Type", contentType);
        }
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPatchRequest(String url, String body, String contentType, String authorizationHeader, Map<String, Object> queryParams) throws IOException, InterruptedException {
        String queryString = "";
        if (queryParams != null && !queryParams.isEmpty()) {
            queryString = queryParams.entrySet().stream()
                    .map(entry -> String.format("%s=%s",
                            URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
                            URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8)))
                    .collect(Collectors.joining("&"));
        }

        URI uri = URI.create(url + (queryString.isEmpty() ? "" : "?" + queryString));

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body == null ? "" : body, StandardCharsets.UTF_8));

        if (contentType != null && !contentType.isEmpty()) {
            requestBuilder.header("Content-Type", contentType);
        }
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private HttpResponse<String> sendDeleteRequest(String url, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE();
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }


    @Test
    void can_CreateInitialUser_and_BlockExtraCreationsWithoutAuthentication() throws IOException, InterruptedException {

        User user = new User("admin@admin.com", "admin@admin.com", "admin@admin.com");

        final HttpResponse<String> response = sendPostRequest(
                elepy.url() + "/users",
                json(user),
                "application/json",
                null // No authentication for the very first user creation
        );

        final HttpResponse<String> response2 = sendPostRequest(
                elepy.url() + "/users",
                json(user),
                "application/json",
                null // Still no authentication, should be blocked
        );


        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response2.statusCode()).isEqualTo(401);
        assertThat(userCrud.count()).isEqualTo(1);

    }

    @Test
    void can_Login_and_FindOtherUsers() throws IOException, InterruptedException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedFind = sendGetRequest(
                elepy.url() + "/users",
                null
        );
        final HttpResponse<String> authorizedFind = sendGetRequest(
                elepy.url() + "/users",
                createBasicAuthHeader("admin@admin.com", "admin@admin.com")
        );

        assertThat(authorizedFind.statusCode()).isEqualTo(200);
        assertThat(unauthorizedFind.statusCode()).isEqualTo(401);
    }

    @Test
    void can_Login_and_DeleteUsers_AsModerator() throws IOException, InterruptedException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedDelete = sendDeleteRequest(
                elepy.url() + "/users/user",
                null
        );

        assertThat(unauthorizedDelete.statusCode()).isEqualTo(401);


        final HttpResponse<String> authorizedDelete = sendDeleteRequest(
                elepy.url() + "/users/user",
                createBasicAuthHeader("admin@admin.com", "admin@admin.com")
        );


        final List<? extends User> all1 = userCrud.getAll();
        assertThat(authorizedDelete.statusCode()).isEqualTo(200);

        assertThat(userCrud.count()).isEqualTo(1);

    }

    @Test
    void can_GetToken_and_Login() throws IOException, InterruptedException {
        elepy.createInitialUserViaHttp("ryan", "susana");

        elepy.get("/random-secured-route", context -> {
            context.result(Message.of("Perfect!", 200));
        });

        // Get token
        final var getTokenResponse = sendPostRequest(
                elepy.url() + "/elepy/token-login",
                null, // No request body for basic auth
                null,
                createBasicAuthHeader("ryan", "susana")
        );

        assertThat(getTokenResponse.statusCode())
                .isEqualTo(200);

        final var token = objectMapper.readValue(getTokenResponse.body(), String.class); // Assuming token is a plain string in the body

        // Access secured route with token
        final var authenticationResponse = sendGetRequest(
                elepy.url() + "/random-secured-route",
                "Bearer " + token
        );

        assertThat(authenticationResponse.statusCode()).isEqualTo(200);
    }

    @Test
    void can_Login_and_UpdateOtherUserRoles() throws IOException, InterruptedException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "password"); // Assuming password is a placeholder

        final HttpResponse<String> unauthorizedFind = sendPutRequest(
                elepy.url() + "/users" + "/user",
                json(userToUpdate),
                "application/json",
                null
        );

        final HttpResponse<String> authorizedFind = sendPutRequest(
                elepy.url() + "/users" + "/user",
                json(userToUpdate),
                "application/json",
                createBasicAuthHeader("admin@admin.com", "admin@admin.com")
        );

        final User user = userCrud.getById("user").orElseThrow();

        assertThat(authorizedFind.statusCode()).isEqualTo(200);
        assertThat(unauthorizedFind.statusCode()).isEqualTo(401);
        assertThat(userCrud.count()).isEqualTo(2);
    }


    @Test
    void cant_Login_and_DeleteYourself() throws IOException, InterruptedException {
        createInitialUsersViaHttp();

        final HttpResponse<String> authorizedFind = sendDeleteRequest(
                elepy.url() + "/users" + "/admin@admin.com",
                createBasicAuthHeader("admin@admin.com", "admin@admin.com")
        );

        assertThat(authorizedFind.statusCode()).isEqualTo(403);
        assertThat(userCrud.count()).isEqualTo(2);
    }


    @Test
    void can_Login_and_UpdateOwnPassword_AsSuperUser() throws IOException, InterruptedException {
        createInitialUsersViaHttp();

        final HttpResponse<String> patchResponse = sendPatchRequest(
                elepy.url() + "/users" + "/admin@admin.com",
                null, // Empty body, query param handles the update
                "application/json",
                createBasicAuthHeader("admin@admin.com", "admin@admin.com"),
                Map.of("password", "newPassword")
        );

        final var admin = userCrud.getById("admin@admin.com").orElseThrow();
        assertThat(patchResponse.statusCode()).isEqualTo(200);
        assertThat(BCrypt.checkpw("newPassword", admin.getPassword()))
                .isTrue();
    }

    @Test
    void can_AccessActions_as_Intended() throws IOException, InterruptedException {
        final HttpResponse<String> getRequest = sendGetRequest(elepy.url() + "/resources/actions/extra-action?ids=999,777", null);

        assertThat(getRequest.statusCode()).isEqualTo(200);
        assertThat(getRequest.body()).isEqualTo("[999,777]");
    }

    @Test
    public void can_FindItems_as_Intended() throws IOException, InterruptedException {
        final long count = resourceCrud.count();
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = sendGetRequest(elepy.url() + "/resources", null);

        List results = objectMapper.readValue(getRequest.body(), List.class);

        assertThat(getRequest.statusCode()).as(getRequest.body()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(count + 1);

    }

    @Test
    public void can_FilterAndSearchItems_AsIntended() throws IOException, InterruptedException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        String queryString = "id_equals=4&uniqueField_contains=filter&numberMax40_equals=25&q=ilterUni";
        final HttpResponse<String> getRequest = sendGetRequest(elepy.url() + "/resources?" + queryString, null);

        List<Resource> results = objectMapper.readValue(getRequest.body(), new TypeReference<List<Resource>>() {
        });

        assertThat(getRequest.statusCode()).as(getRequest.body()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getUniqueField()).isEqualTo("filterUnique");
    }

    @Test
    public void canNot_FindItems_when_QueryDoesntMatch() throws IOException, InterruptedException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        final HttpResponse<String> getRequest = sendGetRequest(elepy.url() + "/resources?q=ilterUni", null);

        List<Resource> results = objectMapper.readValue(getRequest.body(), new TypeReference<List<Resource>>() {
        });

        assertThat(getRequest.statusCode()).as(getRequest.body()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void can_SearchItems_as_Intended() throws IOException, InterruptedException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = sendGetRequest(elepy.url() + "/resources?q=testsearchto", null);

        List<Resource> results = objectMapper.readValue(getRequest.body(), new TypeReference<List<Resource>>() {
        });

        assertThat(getRequest.statusCode()).as(getRequest.body()).isEqualTo(200);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void can_FindItem_byId() throws IOException, InterruptedException {
        resourceCrud.create(validObject());

        final Resource resource = resourceCrud.getAll().get(0);
        final HttpResponse<String> getRequest = sendGetRequest(elepy.url() + "/resources/" + resource.getId(), null);

        Resource foundResource = objectMapper.readValue(getRequest.body(), Resource.class);

        assertThat(getRequest.statusCode()).as(getRequest.body()).isEqualTo(200);
        assertThat(resource.getId()).isEqualTo(foundResource.getId());

    }

    @Test
    public void can_CreateItem() throws IOException, InterruptedException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueCreate");
        final String s = json(resource);

        final HttpResponse<String> postRequest = sendPostRequest(
                elepy.url() + "/resources",
                s,
                "application/json",
                null
        );

        assertThat(postRequest.statusCode()).as(postRequest.body()).isEqualTo(201);
        assertThat(resourceCrud.count()).isEqualTo(count + 1);
    }


    @Test
    public void doesNot_CreateMultipleItems_when_ThereAreIntegrityIssues() throws IOException, InterruptedException {

        final long count = resourceCrud.count();

        final Resource resource = validObject();
        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate"); // Same unique field as resource

        final String s = json(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = sendPostRequest(
                elepy.url() + "/resources",
                s,
                "application/json",
                null
        );

        // Expecting a 400 or similar for integrity issues, not 201
        assertThat(postRequest.statusCode()).isGreaterThanOrEqualTo(400);
        assertThat(resourceCrud.count()).isEqualTo(count);
    }

    @Test
    public void can_CreateMultipleItems_inOneRequest() throws IOException, InterruptedException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate1"); // Different unique field

        final String s = json(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = sendPostRequest(
                elepy.url() + "/resources",
                s,
                "application/json",
                null
        );

        assertThat(postRequest.statusCode()).as(postRequest.body()).isEqualTo(201);
        assertThat(resourceCrud.count()).isEqualTo(count + 2);
    }

    @Test
    void can_DeleteItem() throws IOException, InterruptedException {

        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(55);

        resourceCrud.create(resource);

        assertThat(resourceCrud.count()).isEqualTo(beginningCount + 1);
        final HttpResponse<String> delete = sendDeleteRequest(elepy.url() + "/resources/55", null);

        assertThat(delete.statusCode()).as(delete.body()).isEqualTo(200);
        assertThat(resourceCrud.count()).isEqualTo(beginningCount);

    }

    @Test
    void can_UpdateItemPartially_without_AffectingOtherFields() throws IOException, InterruptedException {
        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(66);
        resource.setMARKDOWN("ryan");


        resourceCrud.create(resource);

        assertThat(resourceCrud.count()).isEqualTo(beginningCount + 1);
        final HttpResponse<String> patch = sendPatchRequest(
                elepy.url() + "/resources/66",
                "{\"id\":" + resource.getId() + ",\"uniqueField\": \"uniqueUpdate\"}",
                "application/json",
                null,
                null // No query params for this PATCH
        );

        assertThat(resourceCrud.count()).isEqualTo(beginningCount + 1);

        Optional<Resource> updatePartialId = resourceCrud.getById(66);

        assertThat(patch.statusCode()).as(patch.body()).isEqualTo(200);
        assertThat(updatePartialId.isPresent()).isTrue();
        assertThat(updatePartialId.get().getUniqueField()).isEqualTo("uniqueUpdate");
        assertThat(updatePartialId.get().getMARKDOWN()).isEqualTo("ryan");
    }

    @Test
    void can_SortDescending() {
        final Resource resource1 = validObject();
        final Resource resource2 = validObject();

        resource1.setTextField("resource1");
        resource2.setTextField("resource2");


        resourceCrud.create(resource1, resource2);

        final List<Resource> search = resourceCrud.find(create(search(""))
                .limit(2)
                .sort("textField", SortOption.DESCENDING));


        assertThat(search.get(0).getTextField())
                .isEqualTo("resource2");

        assertThat(search.get(1).getTextField())
                .isEqualTo("resource1");

    }

    @Test
    void can_SortAscending() {
        final Resource resource1 = validObject();
        final Resource resource2 = validObject();


        resource2.setTextField("resource2");
        resource1.setTextField("resource1");


        resourceCrud.create(resource1, resource2);

        final List<Resource> search = resourceCrud.find(create(search("")).limit(2).sort("textField", SortOption.ASCENDING));

        assertThat(search.get(0).getTextField())
                .isEqualTo("resource1");
        assertThat(search.get(1).getTextField())
                .isEqualTo("resource2");


    }

    private synchronized Resource validObject() {
        Resource resource = new Resource();

        resource.setId(resourceCounter++);
        resource.setMaxLen40("230428");
        resource.setMinLen20("My name is ryan and this is a string  with more than 20 chars");
        resource.setMinLen10MaxLen50("12345678910111213");
        resource.setNumberMax40(BigDecimal.valueOf(40));
        resource.setNumberMin20(BigDecimal.valueOf(20));
        resource.setNumberMin10Max50(BigDecimal.valueOf(15));
        resource.setUniqueField("unique");
        resource.setMARKDOWN("MARKDOWN");
        resource.setTextArea("textarea");
        resource.setTextField("textfield");
        resource.setSearchableField("searchable");

        resource.setNonEditable("nonEditable");

        return resource;
    }


    private String json(Object o) throws JsonProcessingException {
        // Use the instance ObjectMapper
        return objectMapper.writeValueAsString(o);
    }

    private void createInitialUsersViaHttp() throws IOException, InterruptedException {
        User user = new User("admin@admin.com", "admin@admin.com", "admin@admin.com");

        final HttpResponse<String> response = sendPostRequest(
                elepy.url() + "/users",
                json(user),
                "application/json",
                null
        );

        User user2 = new User("user", "user", "user");

        final HttpResponse<String> response2 = sendPostRequest(
                elepy.url() + "/users",
                json(user2),
                "application/json",
                createBasicAuthHeader("admin@admin.com", "admin@admin.com")
        );

        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        assertThat(response2.statusCode()).as(response2.body()).isEqualTo(200);
        assertThat(userCrud.count()).isEqualTo(2);
    }
}