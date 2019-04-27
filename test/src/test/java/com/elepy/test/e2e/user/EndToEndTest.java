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
    private Crud<Resource> defaultMongoDao;


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
        defaultMongoDao = elepy.getCrudFor(Resource.class);
    }

    @AfterAll
    void tearDown() {
        elepy.stop();

    }

    public abstract Configuration configuration();

    @BeforeEach
    void setUp() {
        userCrud.delete(userCrud.getAll().stream().map(User::getId).collect(Collectors.toList()));
    }

    @Test
    void testCanCreateInitialUserAndBlockExtraCreations() throws UnirestException, JsonProcessingException, InterruptedException {

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
    void testLoginAndFind() throws UnirestException, JsonProcessingException {
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
    void testLoginAndDelete() throws JsonProcessingException, UnirestException {
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
    void testLoginAndUpdatePermissions() throws JsonProcessingException, UnirestException {
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
    public void testFind() throws IOException, UnirestException {


        final long count = defaultMongoDao.count();
        defaultMongoDao.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources").asString();


        Page resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), Page.class);

        assertEquals(count + 1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testFilterAndSearch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        resource.setId(4);
        defaultMongoDao.create(resource);
        defaultMongoDao.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?id_equals=4&uniqueField_contains=filter&numberMax40_equals=25&q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
        assertEquals("filterUnique", resourcePage.getValues().get(0).getUniqueField());
    }

    @Test
    public void testSearchNotFindingAnything() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchNotFindingAnything");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        defaultMongoDao.create(resource);
        defaultMongoDao.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?q=ilterUni").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(0, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testSearch() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("testSearchTo2");
        resource.setNumberMax40(BigDecimal.valueOf(25));
        defaultMongoDao.create(resource);
        defaultMongoDao.create(validObject());
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources?q=testsearchto").asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testFindOne() throws IOException, UnirestException {


        defaultMongoDao.create(validObject());

        final Resource resource = defaultMongoDao.getAll().get(0);
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources/" + resource.getId()).asString();

        Resource foundResource = elepy.getObjectMapper().readValue(getRequest.getBody(), Resource.class);

        assertEquals(foundResource.getId(), resource.getId());

        Assertions.assertEquals(200, getRequest.getStatus());
    }

    @Test
    public void testCreate() throws UnirestException, JsonProcessingException {

        final long count = defaultMongoDao.count();
        final Resource resource = validObject();
        resource.setUniqueField("uniqueCreate");
        final String s = elepy.getObjectMapper().writeValueAsString(resource);

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        assertEquals(count + 1, defaultMongoDao.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }


    @Test
    public void testMultiCreate_atomicCreateInsertsNone_OnIntegrityFailure() throws UnirestException, JsonProcessingException {

        final long count = defaultMongoDao.count();

        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate");


        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        assertEquals(count, defaultMongoDao.count());
    }

    @Test
    public void testMultiCreate() throws UnirestException, JsonProcessingException {

        final long count = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setUniqueField("uniqueMultiCreate");

        final Resource resource1 = validObject();
        resource1.setUniqueField("uniqueMultiCreate1");

        final String s = elepy.getObjectMapper().writeValueAsString(new Resource[]{resource, resource1});

        final HttpResponse<String> postRequest = Unirest.post(url + "/resources").body(s).asString();

        System.out.println(postRequest.getBody());
        assertEquals(count + 2, defaultMongoDao.count());
        Assertions.assertEquals(201, postRequest.getStatus());
    }

    @Test
    void testDelete() throws UnirestException {

        final long beginningCount = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setId(55);

        defaultMongoDao.create(resource);

        assertEquals(beginningCount + 1, defaultMongoDao.count());
        final HttpResponse<String> delete = Unirest.delete(url + "/resources/55").asString();

        assertEquals(beginningCount, defaultMongoDao.count());
        Assertions.assertEquals(200, delete.getStatus());

    }

    @Test
    void testUpdatePartial() throws UnirestException {
        final long beginningCount = defaultMongoDao.count();
        final Resource resource = validObject();

        resource.setId(66);
        resource.setMARKDOWN("ryan");


        defaultMongoDao.create(resource);

        assertEquals(beginningCount + 1, defaultMongoDao.count());
        final HttpResponse<String> patch = Unirest.patch(url + "/resources/66").body("{\"id\":" + resource.getId() + ",\"uniqueField\": \"uniqueUpdate\"}").asString();

        assertEquals(beginningCount + 1, defaultMongoDao.count());


        Optional<Resource> updatePartialId = defaultMongoDao.getById(66);

        assertTrue(updatePartialId.isPresent());
        assertEquals("uniqueUpdate", updatePartialId.get().getUniqueField());
        assertEquals("ryan", updatePartialId.get().getMARKDOWN());
        Assertions.assertEquals(200, patch.getStatus());
    }

    @Test
    void testExtraRouteInService() throws UnirestException {

        final String shouldReturn = "I am here";
        Resource resource1 = validObject();
        resource1.setId(77);
        resource1.setTextField(shouldReturn);
        defaultMongoDao.create(resource1);
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources/" + resource1.getId() + "/extra").asString();

        Assertions.assertEquals(200, getRequest.getStatus());
        Assertions.assertEquals(shouldReturn, getRequest.getBody());
    }

    @Test
    void testExtraRoute() throws UnirestException {
        final HttpResponse<String> getRequest = Unirest.get(url + "/resources-extra").asString();

        Assertions.assertEquals(201, getRequest.getStatus());
        Assertions.assertEquals("generated", getRequest.getBody());
    }

    @Test
    void testAction() throws UnirestException {
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

    public synchronized Resource validObject() {
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
