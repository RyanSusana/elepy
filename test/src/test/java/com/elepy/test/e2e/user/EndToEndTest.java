package com.elepy.test.e2e.user;

import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class EndToEndTest {

    private static int counter = 7300;
    private final int port;
    private final String url;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Elepy elepy;

    private Crud<User> userCrud;


    public EndToEndTest() {
        port = counter++;

        url = String.format("http://localhost:%d/users", port);
    }



    @BeforeAll
    public void beforeAll() {

        elepy = new Elepy()
                .addConfiguration(configuration())
                .onPort(port);


        elepy.start();
        userCrud = elepy.getCrudFor(User.class);
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
                .post(url)
                .body(json(user))
                .asString();

        final HttpResponse<String> response2 = Unirest
                .post(url)
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
                .get(url).asString();
        final HttpResponse<String> authorizedFind = Unirest
                .get(url)
                .basicAuth("admin", "admin")
                .asString();

        Assertions.assertEquals(200, authorizedFind.getStatus());
        Assertions.assertEquals(401, unauthorizedFind.getStatus());
    }

    @Test
    void testLoginAndDelete() throws JsonProcessingException, UnirestException {
        createInitialUsersViaHttp();


        final HttpResponse<String> unauthorizedDelete = Unirest
                .delete(url)
                .asString();
        final HttpResponse<String> authorizedDelete = Unirest
                .delete(url + "/user")
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
                .put(url + "/user")
                .body(json(userToUpdate))
                .asString();

        final HttpResponse<String> authorizedFind = Unirest
                .put(url + "/user")
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

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private void createInitialUsersViaHttp() throws JsonProcessingException, UnirestException {
        User user = new User("admin", "admin", "admin", Collections.emptyList());

        final HttpResponse<String> response = Unirest
                .post(url)
                .body(json(user))
                .asString();

        User user2 = new User("user", "user", "user", Collections.emptyList());

        final HttpResponse<String> response2 =
                Unirest.post(url)
                        .basicAuth("admin", "admin")
                        .body(json(user2))
                        .asString();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(200, response2.getStatus());
    }
}
