package com.elepy.test.e2e.user;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class EndToEndTest {

    private static int portCounter = 7300;

    private static int resourceCounter = -100;
    private final int port;
    private final String userUrl;
    private final String url;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Elepy elepy;

    private Crud<User> userCrud;
    private Crud<Resource> resourceCrud;


    public EndToEndTest() {
        port = portCounter++;


        url = String.format("http://localhost:%d", port);
        userUrl = url + "/users";
    }


    @BeforeAll
    public void beforeAll() {

        elepy = new Elepy()
                .addModel(Resource.class)
                .addConfiguration(configuration())
                .onPort(port);


        elepy.start();
        userCrud = elepy.getCrudFor(User.class);
        resourceCrud = elepy.getCrudFor(Resource.class);
    }

    @AfterAll
    void tearDown() {
        elepy.stop();

    }

    public abstract Configuration configuration();

    @BeforeEach
    void setUp() {
        userCrud.delete(userCrud.getAll().stream().map(User::getId).collect(Collectors.toList()));
        resourceCrud.delete(resourceCrud.getAll().stream().map(Resource::getId).collect(Collectors.toList()));
    }

    @Test
    void can_CreateInitialUser_and_BlockExtraCreationsWithoutAuthentication() throws UnirestException, JsonProcessingException, InterruptedException {

        User user = new User("admin", "admin", "admin", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(userUrl)
                .body(json(user))
                .asString();

        final HttpResponse<String> response2 = Unirest
                .post(userUrl)
                .body(json(user))
                .asString();


        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(401, response2.getStatus());
        Assertions.assertEquals(1, userCrud.count());

    }

    @Test
    void can_Login_and_FindOtherUsers() throws UnirestException, JsonProcessingException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedFind = Unirest
                .get(userUrl).asString();
        final HttpResponse<String> authorizedFind = Unirest
                .get(userUrl)
                .basicAuth("admin", "admin")
                .asString();

        Assertions.assertEquals(200, authorizedFind.getStatus());
        Assertions.assertEquals(401, unauthorizedFind.getStatus());
    }

    @Test
    void can_Login_and_DeleteUsers() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedDelete = Unirest
                .delete(userUrl)
                .asString();
        final HttpResponse<String> authorizedDelete = Unirest
                .delete(userUrl + "/user")
                .basicAuth("admin", "admin")
                .asString();

        Assertions.assertEquals(200, authorizedDelete.getStatus());

        Assertions.assertEquals(401, unauthorizedDelete.getStatus());
        Assertions.assertEquals(1, userCrud.count());

    }

    @Test
    void can_Login_and_UpdateOtherUserPermissions() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();

        User userToUpdate = new User("user", "user", "", Collections.singletonList(Permissions.SUPER_USER));

        final HttpResponse<String> unauthorizedFind = Unirest
                .put(userUrl + "/user")
                .body(json(userToUpdate))
                .asString();

        final HttpResponse<String> authorizedFind = Unirest
                .put(userUrl + "/user")
                .basicAuth("admin", "admin")
                .body(json(userToUpdate))
                .asString();

        final Optional<User> user = userCrud.getById("user");

        Assertions.assertEquals(200, authorizedFind.getStatus());
        Assertions.assertEquals(401, unauthorizedFind.getStatus());
        Assertions.assertEquals(2, userCrud.count());
        assertTrue(user.isPresent());
        assertEquals(1, user.get().getPermissions().size());
        assertEquals(Permissions.SUPER_USER, user.get().getPermissions().get(0));

    }


    @Test
    public void can_FindItems_as_Intended() throws IOException, UnirestException {


        final long count = resourceCrud.count();
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources").asString();


        Page resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), Page.class);

        assertEquals(count + 1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_FilterAndSearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?id_equals=4&uniqueField_contains=filter&numberMax40_equals=25&q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
        assertEquals("filterUnique", resourcePage.getValues().get(0).getUniqueField());
    }

    @Test
    public void canNot_FindItems_when_QueryDoesntMatch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        System.out.println(getRequest.getBody());
        assertEquals(0, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_SearchItems_as_Intended() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resourceCrud.create(resource);
        resourceCrud.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?q=testsearchto").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_FindItem_byId() throws IOException, UnirestException {
        resourceCrud.create(validObject());

        final Resource resource = resourceCrud.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        assertEquals(foundResource.getId(), resource.getId());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void can_CreateItem() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueCreate");
        final String s = elepy.getObjectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        assertEquals(count + 1, resourceCrud.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }


    @Test
    public void doesNot_CreateMultipleItems_when_ThereAreIntegrityIssues() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();

        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate");


        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        assertEquals(count, resourceCrud.count());
    }

    @Test
    public void can_CreateMultipleItems_inOneRequest() throws UnirestException, JsonProcessingException {

        final long count = resourceCrud.count();
        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate1");

        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        System.out.println(postRequest.getBody());
        assertEquals(count + 2, resourceCrud.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }

    @Test
    void can_DeleteItem() throws UnirestException {

        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(55);

        resourceCrud.create(resource);

        assertEquals(beginningCount + 1, resourceCrud.count());
        final HttpResponse<String> delete = Unirest.delete(url + "/resources/55").asString();

        assertEquals(beginningCount, resourceCrud.count());
        Assertions.assertEquals(200, delete.getStatus());

    }

    @Test
    void can_UpdateItemPartially_without_AffectingOtherFields() throws UnirestException {
        final long beginningCount = resourceCrud.count();
        final Resource resource = validObject();

        resource.setId(66);
        resource.setMARKDOWN("ryan");


        resourceCrud.create(resource);

        assertEquals(beginningCount + 1, resourceCrud.count());
        final HttpResponse<String> patch = Unirest.patch(url + "/resources/66").body("{\"id\":" + resource.getId() + ",\"uniqueField\": \"uniqueUpdate\"}").asString();

        assertEquals(beginningCount + 1, resourceCrud.count());


        Optional<Resource> updatePartialId = resourceCrud.getById(66);

        assertTrue(updatePartialId.isPresent());
        assertEquals("uniqueUpdate", updatePartialId.get().getUniqueField());
        assertEquals("ryan", updatePartialId.get().getMARKDOWN());
        Assertions.assertEquals(200, patch.getStatus());
    }

    @Test
    void can_AccessExtraRoutes_when_RoutesAreDefinedInAService() throws UnirestException {

        final String shouldReturn = "I am here";
        Resource resource1 = validObject();
        resource1.setId(77);
        resource1.setTextField(shouldReturn);
        resourceCrud.create(resource1);
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources/" + resource1.getId() + "/extra").asString();

        Assertions.assertEquals(200, getRequest.getStatus());
        Assertions.assertEquals(shouldReturn, getRequest.getBody());
    }

    @Test
    void can_AccessExtraRoutes_as_Intended() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources-extra").asString();

        Assertions.assertEquals(201, getRequest.getStatus());
        Assertions.assertEquals("generated", getRequest.getBody());
    }

    @Test
    void can_AccessActions_as_Intended() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources/actions/extra-action?ids=999,777").asString();

        Assertions.assertEquals(200, getRequest.getStatus());
        Assertions.assertEquals("[999,777]", getRequest.getBody());
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private void createInitialUsersViaHttp() throws JsonProcessingException, UnirestException {
        User user = new User("admin", "admin", "admin", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(userUrl)
                .body(json(user))
                .asString();

        User user2 = new User("user", "user", "user", Collections.emptyList());

        final HttpResponse<String> response2 =
                Unirest.post(userUrl)
                        .basicAuth("admin", "admin")
                        .body(json(user2))
                        .asString();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(200, response2.getStatus());
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
        resource.setRequiredField("required");

        resource.setNonEditable("nonEditable");

        return resource;
    }
}
