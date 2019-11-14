package com.elepy.tests.auth;

import com.elepy.auth.Permissions;
import com.elepy.auth.User;
import com.elepy.exceptions.Message;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
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

        elepy.start();

        Unirest.setHttpClient(HttpClients.custom().disableCookieManagement().build());
    }

    @Test
    void can_Login_withToken() throws UnirestException {
        elepy.createInitialUserViaHttp("michelle", "bowers");

        var reference = new AtomicReference<User>();

        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(Permissions.AUTHENTICATED);

            reference.set(context.loggedInUserOrThrow());
            context.result(Message.of("Perfect!", 200));
        });


        final var getTokenResponse = Unirest.post(elepy + "/elepy-token-login")
                .basicAuth("michelle", "bowers")
                .asString();


        final var token = getTokenResponse.getBody().replaceAll("\"", "");

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route").header("ELEPY_TOKEN", token).asString();

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


        final var getTokenResponse = Unirest.post(elepy + "/elepy-token-login")
                .basicAuth("michelle", "bows")
                .asString();

        assertThat(getTokenResponse.getStatus())
                .isEqualTo(401);

//        assertThat(getTokenResponse.getBody().getObject().getString("message"))
//                .contains("invalid");

    }

    @Test
    void cannot_AccessSecuredRoute_withWrongToken() throws UnirestException {
        var reference = new AtomicReference<User>();

        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(Permissions.AUTHENTICATED);

            reference.set(context.loggedInUserOrThrow());
            context.result(Message.of("Perfect!", 200));
        });

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route")
                .header("ELEPY_TOKEN", "a_wrong_token").asString();

        assertThat(authenticationResponse.getStatus())
                .isEqualTo(401);
    }

    @Test
    void cannot_AccessSecuredRoute_withoutToken() throws UnirestException {
        var reference = new AtomicReference<User>();

        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(Permissions.AUTHENTICATED);

            reference.set(context.loggedInUserOrThrow());
            context.result(Message.of("Perfect!", 200));
        });

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route")
                .asString();

        assertThat(authenticationResponse.getStatus())
                .isEqualTo(401);
    }
}
