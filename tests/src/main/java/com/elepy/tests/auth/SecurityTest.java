package com.elepy.tests.auth;

import com.elepy.auth.permissions.DefaultPermissions;
import com.elepy.auth.permissions.Permissions;
import com.elepy.auth.User;
import com.elepy.exceptions.Message;
import com.elepy.tests.CustomUser;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SecurityTest implements ElepyConfigHelper {

    private ElepySystemUnderTest elepy;

    @BeforeEach
    void before() {
        elepy = ElepySystemUnderTest.create();

        this.configureElepy(elepy);
        elepy.addModel(CustomUser.class);

        elepy.start();

        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());
    }

    @Test
    void can_Login_withToken() throws UnirestException {
        elepy.createInitialUserViaHttp("michelle", "bowers");

        var reference = new AtomicReference<User>();

        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(DefaultPermissions.AUTHENTICATED);

            reference.set(context.loggedInUserOrThrow());
            context.result(Message.of("Perfect!", 200));
        });


        final var getTokenResponse = Unirest.post(elepy + "/elepy/token-login")
                .basicAuth("michelle", "bowers")
                .asString();


        final var token = getTokenResponse.getBody().replaceAll("\"", "");

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route").header("Authorization", "Bearer " + token).asString();

        assertThat(authenticationResponse.getStatus())
                .isEqualTo(200);
        assertThat(authenticationResponse.getBody())
                .contains("Perfect!");
        assertThat(reference.get().getUsername())
                .isEqualTo("michelle");
    }

    @Test
    void cannot_RetrieveToken_withInvalidCredentials() throws UnirestException {
        elepy.createInitialUserViaHttp("michelle", "bowers");


        final var getTokenResponse = Unirest.post(elepy + "/elepy/token-login")
                .basicAuth("michelle", "bows")
                .asString();

        assertThat(getTokenResponse.getStatus())
                .isEqualTo(401);

    }

    @Test
    void cannot_AccessSecuredRoute_withWrongToken() throws UnirestException {
        var reference = new AtomicReference<User>();

        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(DefaultPermissions.AUTHENTICATED);

            reference.set(context.loggedInUserOrThrow());
            context.result(Message.of("Perfect!", 200));
        });

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route")
                .header("Authorization", "Bearer a_wrong_token").asString();

        assertThat(authenticationResponse.getStatus())
                .isEqualTo(401);
    }

    @Test
    void cannot_AccessSecuredRoute_withoutToken() throws UnirestException {
        var reference = new AtomicReference<User>();

        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(DefaultPermissions.AUTHENTICATED);

            reference.set(context.loggedInUserOrThrow());
            context.result(Message.of("Perfect!", 200));
        });

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route")
                .asString();

        assertThat(authenticationResponse.getStatus())
                .isEqualTo(401);
    }

    @Test
    void can_AccessSelf() throws JsonProcessingException, UnirestException {

        final var user1 = new CustomUser();

        user1.setId("user");
        user1.setUsername("user");
        user1.setEmail("ryansemail@live.com");
        user1.setPassword("userPassword");

        Unirest
                .post(elepy + "/users")
                .basicAuth("user", "user")
                .body(new ObjectMapper().writeValueAsString(user1))
                .asString();


        final var response = Unirest.get(elepy + "/elepy/logged-in-user").basicAuth("user", "userPassword").asJson();

        assertThat(response.getStatus())
                .isEqualTo(200);

        final var responseBody = response.getBody().getObject();
        assertThat(responseBody.getString("email"))
                .isEqualTo("ryansemail@live.com");


    }
}
