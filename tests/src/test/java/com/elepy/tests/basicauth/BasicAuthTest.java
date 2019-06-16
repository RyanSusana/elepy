package com.elepy.tests.basicauth;

import com.elepy.Elepy;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import com.elepy.auth.methods.BasicAuthenticationMethod;
import com.elepy.exceptions.Message;
import com.elepy.http.AccessLevel;
import com.elepy.http.HttpMethod;
import com.elepy.mongo.MongoCrudFactory;
import com.github.fakemongo.Fongo;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.DB;
import com.mongodb.FongoDB;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.elepy.http.RouteBuilder.anElepyRoute;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicAuthTest {


    protected Elepy elepy;

    @BeforeEach
    void setUp() {

        elepy = new Elepy();
        elepy.withDefaultCrudFactory(MongoCrudFactory.class);

        Fongo fongo = new Fongo("test");
        final FongoDB db = fongo.getDB("test");

        elepy.registerDependency(DB.class, db);
        elepy.onPort(10293);

    }

    @AfterEach
    void tearDown() {
        elepy.stop();
    }


    protected UserLoginService mockedLoginService(List<String> permissionsOnLogin) {

        final var mock = Mockito.mock(UserLoginService.class);

        Mockito.when(mock.login(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.empty());
        Mockito.when(mock.login("admin", "admin")).thenReturn(Optional.of(new User("admin", "admin", "admin", permissionsOnLogin)));


        return mock;
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

        Awaitility.await().until(() -> elepy.isInitialized());

        final HttpResponse<String> response = Unirest.get("http://localhost:10293/test")
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

        Awaitility.await().until(() -> elepy.isInitialized());

        final HttpResponse<String> response = Unirest.get("http://localhost:10293/test")
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


        Awaitility.await().until(() -> elepy.isInitialized());

        final HttpResponse<String> response = Unirest.get("http://localhost:10293/test")
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
        Awaitility.await().until(() -> elepy.isInitialized());


        final HttpResponse<String> response = Unirest.get("http://localhost:10293/test").asString();

        assertEquals(401, response.getStatus());
    }


}
