package com.elepy.auth.methods;

import com.elepy.Elepy;
import com.elepy.auth.MockCrudProvider;
import com.elepy.exceptions.Message;
import com.elepy.http.AccessLevel;
import com.elepy.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.elepy.http.RouteBuilder.anElepyRoute;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicAuthEndToEndTest extends BaseMethodTest {

    @BeforeEach
    void setUp() {

        elepy = new Elepy();
        elepy.onPort(3997);

        elepy.withDefaultCrudProvider(MockCrudProvider.class);


    }

    @AfterEach
    void tearDown() {
        elepy.stop();
    }

    @Test
    void testSuccessfulLogin() throws UnirestException {

        elepy.addRouting(anElepyRoute()
                .accessLevel(AccessLevel.PUBLIC)
                .path("/test")
                .method(HttpMethod.GET)
                .addPermissions("needThisPermission")
                .route(ctx -> ctx.result(Message.of("Good", 200)))
                .build()
        );
        elepy.userLogin()
                .addAuthenticationMethod(new BasicAuthenticationMethod(
                                mockedLoginService(
                                        Collections.singletonList("needThisPermission")
                                )
                        )
                );
        elepy.start();


        final HttpResponse<String> response = Unirest.get("http://localhost:3997/test")
                .basicAuth("admin", "admin").asString();

        assertEquals(200, response.getStatus());
    }

    @Test
    void testWrongPassword() throws UnirestException {

        elepy.addRouting(anElepyRoute()
                .accessLevel(AccessLevel.PUBLIC)
                .path("/test")
                .method(HttpMethod.GET)
                .addPermissions("needThisPermission")
                .route(ctx -> ctx.result(Message.of("Good", 200)))
                .build()
        );
        elepy.userLogin()
                .addAuthenticationMethod(new BasicAuthenticationMethod(
                                mockedLoginService(
                                        Collections.singletonList("needThisPermission")
                                )
                        )
                );
        elepy.start();


        final HttpResponse<String> response = Unirest.get("http://localhost:3997/test")
                .basicAuth("admin", "wrongPassword").asString();

        assertEquals(401, response.getStatus());
    }

    @Test
    void testWrongPermission() throws UnirestException {

        elepy.addRouting(anElepyRoute()
                .accessLevel(AccessLevel.PUBLIC)
                .path("/test")
                .method(HttpMethod.GET)
                .addPermissions("needThisPermission")
                .route(ctx -> ctx.result(Message.of("ss", 200)))
                .build()
        );
        elepy.userLogin()
                .addAuthenticationMethod(new BasicAuthenticationMethod(
                                mockedLoginService(
                                        Arrays.asList("wrongPermission")
                                )
                        )
                );
        elepy.start();


        final HttpResponse<String> response = Unirest.get("http://localhost:3997/test")
                .basicAuth("admin", "admin").asString();

        assertEquals(401, response.getStatus());
    }

    @Test
    void testNoLogin() throws UnirestException {

        elepy.addRouting(anElepyRoute()
                .accessLevel(AccessLevel.PUBLIC)
                .path("/test")
                .method(HttpMethod.GET)
                .addPermissions("needThisPermission")
                .route(ctx -> ctx.result(Message.of("Good", 200)))
                .build()
        );
        elepy.userLogin()
                .addAuthenticationMethod(new BasicAuthenticationMethod(
                                mockedLoginService(
                                        Collections.singletonList("needThisPermission")
                                )
                        )
                );
        elepy.start();


        final HttpResponse<String> response = Unirest.get("http://localhost:3997/test").asString();

        assertEquals(401, response.getStatus());
    }


}
