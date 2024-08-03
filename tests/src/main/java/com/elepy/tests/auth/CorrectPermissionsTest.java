package com.elepy.tests.auth;

import com.elepy.auth.users.User;
import com.elepy.crud.Crud;
import com.elepy.query.Filters;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
public abstract class CorrectPermissionsTest implements ElepyConfigHelper {
    private ElepySystemUnderTest elepy;
    private Crud<User> users;
    private Crud<Password> passwords;


    private final List<User> userBase = new ArrayList<>();
    private static final String NO_USER = "unauthenticated user";
    private static final String USER_CORRECT_ROLE = "user with correct role";
    private static final String USER_INCORRECT_ROLE = "user with incorrect role";
    private static final String USER_NO_ROLE = "user with no roles";
    private static final String ADMIN = "admin";
    private static final String USER_WITH_FIND_ROLE = "user with only find role";

    {
        userBase.add(new User(ADMIN, ADMIN, ADMIN, Collections.emptyList()));
        userBase.add(new User(USER_CORRECT_ROLE, "userCorrect", "userCorrect", List.of("password-admin")));
        userBase.add(new User(USER_INCORRECT_ROLE, "userIncorrect", "userIncorrect", List.of("users")));
        userBase.add(new User(USER_NO_ROLE, "noRole", USER_NO_ROLE, List.of()));
        userBase.add(new User(USER_WITH_FIND_ROLE, "findRole", USER_WITH_FIND_ROLE, List.of("password-viewer")));
    }

    @BeforeAll
    void beforeAll() {
        elepy = ElepySystemUnderTest.create();

        this.configureElepy(elepy);
        elepy.addModel(Password.class);

        elepy.start();
        users = elepy.getCrudFor(User.class);
        passwords = elepy.getCrudFor(Password.class);
        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());


        createInitialUsersViaHttp();
    }

    @BeforeEach
    void before() {
        passwords.delete(Filters.any());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            USER_NO_ROLE,
            USER_INCORRECT_ROLE,
            NO_USER,
            USER_WITH_FIND_ROLE

    })
    void shouldNotBeAbleTo_Create(String userId) throws UnirestException {
        final var response = setupAuth(getUserFromBase(userId), Unirest.post(elepy + "/passwords"))
                .body(json(arbitraryPassword("createIncorrectUser")))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isEqualTo(401);

        assertThat(passwords.count())
                .isEqualTo(0);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            USER_NO_ROLE,
            USER_INCORRECT_ROLE,
            NO_USER,
            USER_WITH_FIND_ROLE
    })
    void shouldNotBeAbleTo_Update(String userId) throws UnirestException {
        final var initial = arbitraryPassword("initial");
        passwords.create(initial);

        initial.setPassword("newPassword");
        final var response = setupAuth(getUserFromBase(userId), Unirest.put(elepy + "/passwords/initial"))
                .body(json(initial))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isEqualTo(401);

        assertThat(passwords.count())
                .isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            USER_NO_ROLE,
            USER_INCORRECT_ROLE,
            NO_USER,
            USER_WITH_FIND_ROLE

    })
    void shouldNotBeAbleTo_Delete(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.delete(elepy + "/passwords/initial"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isEqualTo(401);

        assertThat(passwords.count())
                .isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            USER_NO_ROLE,
            USER_INCORRECT_ROLE,
            NO_USER

    })
    void shouldNotBeAbleTo_FindMany(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.get(elepy + "/passwords"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isEqualTo(401);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            USER_NO_ROLE,
            USER_INCORRECT_ROLE,
            NO_USER

    })
    void shouldNotBeAbleTo_FindOne(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.get(elepy + "/passwords/initial"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isEqualTo(401);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            USER_NO_ROLE,
            USER_INCORRECT_ROLE,
            NO_USER,
            USER_WITH_FIND_ROLE

    })
    void shouldNotBeAbleTo_ExecuteCustomAction(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.post(elepy + "/passwords/actions/custom-action"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isEqualTo(401);

        assertThat(passwords.count())
                .isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {ADMIN, USER_CORRECT_ROLE})
    void shouldBeAbleTo_Create(String userId) throws UnirestException {
        final var response = setupAuth(getUserFromBase(userId), Unirest.post(elepy + "/passwords"))
                .body(json(arbitraryPassword("createIncorrectUser")))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isGreaterThanOrEqualTo(200)
                .isLessThan(300);

        assertThat(passwords.count())
                .isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {ADMIN, USER_CORRECT_ROLE})
    void shouldBeAbleTo_Update(String userId) throws UnirestException {
        final var initial = arbitraryPassword("initial");
        passwords.create(initial);

        initial.setPassword("newPassword");
        final var response = setupAuth(getUserFromBase(userId), Unirest.put(elepy + "/passwords/initial"))
                .body(json(initial))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isGreaterThanOrEqualTo(200)
                .isLessThan(300);

        assertThat(passwords.count())
                .isEqualTo(1);

        assertThat(passwords.getById(initial.getSavedLocation()))
                .hasValueSatisfying(password -> assertThat(password.getPassword()).isEqualTo("newPassword"));
    }


    @ParameterizedTest
    @ValueSource(strings = {ADMIN, USER_CORRECT_ROLE})
    void shouldBeAbleTo_Delete(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.delete(elepy + "/passwords/initial"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isGreaterThanOrEqualTo(200)
                .isLessThan(300);

        assertThat(passwords.count())
                .isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {ADMIN, USER_CORRECT_ROLE, USER_WITH_FIND_ROLE})
    void shouldBeAbleTo_FindMany(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.get(elepy + "/passwords"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isGreaterThanOrEqualTo(200)
                .isLessThan(300);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ADMIN,
            USER_CORRECT_ROLE,
            USER_WITH_FIND_ROLE
    })
    void shouldBeAbleTo_FindOne(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.get(elepy + "/passwords/initial"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isGreaterThanOrEqualTo(200)
                .isLessThan(300);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ADMIN,
            USER_CORRECT_ROLE
    })
    void shouldBeAbleTo_ExecuteCustomAction(String userId) throws UnirestException {
        passwords.create(arbitraryPassword("initial"));

        final var response = setupAuth(getUserFromBase(userId), Unirest.post(elepy + "/passwords/actions/custom-action"))
                .asString();

        assertThat(response.getStatus())
                .as(response.getBody())
                .isGreaterThanOrEqualTo(200)
                .isLessThan(300);

        assertThat(passwords.count())
                .isEqualTo(0);
    }

    private User getAdmin() {
        return getUserFromBase(ADMIN);
    }

    private User getUserFromBase(String userId) {
        if (NO_USER.equals(userId)) {
            return null;
        }
        return userBase.stream().filter(user -> userId.equals(user.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No user with ID '%s' in the mock user base. Consider using: %s", userId, userBase.stream()
                        .map(User::getId).map(id -> "'" + id + "'").collect(Collectors.joining(", ")))));
    }

    private void createInitialUsersViaHttp() {

        try {
            var admin = getAdmin();
            final HttpResponse<String> response = Unirest
                    .post(elepy + "/users")
                    .body(json(getUserFromBase(ADMIN)))
                    .asString();


            userBase.stream().filter(user -> !ADMIN.equals(user.getId())).forEach(user -> {
                try {
                    final var response1 = Unirest.post(elepy + "/users")
                            .basicAuth(admin.getUsername(), admin.getPassword())
                            .body(json(user))
                            .asString();

                    assertThat(response1.getStatus()).as(response1.getBody()).isEqualTo(200);
                } catch (UnirestException e) {
                    throw new RuntimeException(e);
                }

            });

            assertThat(users.count())
                    .isEqualTo(userBase.size());


        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private Password arbitraryPassword(String idForPassword) {

        final var id = idForPassword == null ? UUID.randomUUID().toString() : idForPassword;

        final var password = new Password();

        password.setPassword(id);
        password.setSavedLocation(id);
        password.setUsername(id);
        return password;
    }

    private <T extends HttpRequest> T setupAuth(User user, T request) {
        if (user != null)
            request.basicAuth(user.getUsername(), user.getPassword());
        return request;
    }

    private String json(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
