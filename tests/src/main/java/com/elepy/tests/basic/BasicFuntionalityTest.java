package com.elepy.tests.basic;

import com.elepy.Configuration;
import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.tests.crud.CrudTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BasicFuntionalityTest extends CrudTest {


    protected Crud<User> userCrud;


    public BasicFuntionalityTest(Configuration... configurations) {
        super(configurations);
    }

    @Override
    @BeforeAll
    protected void setUpAll() {
        super.setUpAll();
        userCrud = elepy.getCrudFor(User.class);
    }

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        userCrud.delete(userCrud.getAll().stream().map(User::getId).collect(Collectors.toList()));
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
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(1, user.get().getPermissions().size());
        Assertions.assertEquals(Permissions.SUPER_USER, user.get().getPermissions().get(0));

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


    protected String json(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    protected void createInitialUsersViaHttp() throws JsonProcessingException, UnirestException {
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


}
