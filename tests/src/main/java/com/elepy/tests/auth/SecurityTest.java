package com.elepy.tests.auth;

import com.elepy.auth.Permissions;
import com.elepy.exceptions.Message;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SecurityTest implements ElepyConfigHelper {

    private ElepySystemUnderTest elepy;

    @BeforeEach
    void before() {
        elepy = ElepySystemUnderTest.create();

        this.configureElepy(elepy);

        elepy.start();
    }

    @Test
    void can_Login_withToken() throws UnirestException {
        elepy.createInitialUserViaHttp("michelle", "bowers");


        elepy.get("/random-secured-route", context -> {
            context.requirePermissions(Permissions.AUTHENTICATED);
            context.result(Message.of("Perfect!", 200));
        });
        final var getTokenResponse = Unirest.post(elepy + "/elepy-token-login")
                .basicAuth("michelle", "bowers")
                .asString();


        final var token = getTokenResponse.getBody().replaceAll("\"", "");

        final var authenticationResponse = Unirest.get(elepy + "/random-secured-route").header("ELEPY_TOKEN", token).asString();

        assertEquals(200, authenticationResponse.getStatus());
    }


}
