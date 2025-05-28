package com.elepy.tests.auth;

import com.elepy.auth.users.User;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpMethod;
import com.elepy.http.RouteBuilder;
import com.elepy.tests.CustomUser;
import com.elepy.tests.ElepyConfigHelper;
import com.elepy.tests.ElepySystemUnderTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SecurityTest implements ElepyConfigHelper {

    private ElepySystemUnderTest elepy;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void before() {
        elepy = ElepySystemUnderTest.create();
        this.configureElepy(elepy);
        elepy.addModel(CustomUser.class);
        elepy.start();

        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        elepy.stop();
    }

    private HttpResponse<String> sendGetRequest(String url, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(String url, String body, String contentType, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body, StandardCharsets.UTF_8));

        if (contentType != null && !contentType.isEmpty()) {
            requestBuilder.header("Content-Type", contentType);
        }
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }


    @Test
    void can_Login_withToken() throws IOException, InterruptedException {
        elepy.createInitialUserViaHttp("michelle", "bowers");

        var reference = new AtomicReference<User>();

        elepy.addRoute(RouteBuilder.anElepyRoute()
                .permissions("authenticated")
                .method(HttpMethod.GET)
                .path("/random-secured-route")
                .route(context -> {
                            reference.set(context.request().loggedInUserOrThrow());
                            context.result(Message.of("Perfect!", 200));
                        }
                )
                .build());

        // Get token
        final var getTokenResponse = sendPostRequest(
                elepy.url() + "/elepy/token-login",
                null, // No request body for basic auth
                null,
                createBasicAuthHeader("michelle", "bowers")
        );

        assertThat(getTokenResponse.statusCode())
                .isEqualTo(200);

        final var token = objectMapper.readValue(getTokenResponse.body(), String.class); // Assuming token is a plain string in the body

        // Access secured route with token
        final var authenticationResponse = sendGetRequest(
                elepy.url() + "/random-secured-route",
                "Bearer " + token
        );

        assertThat(authenticationResponse.statusCode())
                .isEqualTo(200);
        assertThat(authenticationResponse.body())
                .contains("Perfect!");
        assertThat(reference.get().getUsername())
                .isEqualTo("michelle");
    }

    @Test
    void cannot_RetrieveToken_withInvalidCredentials() throws IOException, InterruptedException {
        elepy.createInitialUserViaHttp("michelle", "bowers");

        final var getTokenResponse = sendPostRequest(
                elepy.url() + "/elepy/token-login",
                null, // No request body for basic auth
                null,
                createBasicAuthHeader("michelle", "bows")
        );

        assertThat(getTokenResponse.statusCode())
                .isEqualTo(401);
    }

    @Test
    void cannot_AccessSecuredRoute_withWrongToken() throws IOException, InterruptedException {
        var reference = new AtomicReference<User>();

        elepy.addRoute(RouteBuilder.anElepyRoute()
                .permissions("authenticated")
                .method(HttpMethod.GET)
                .path("/random-secured-route")
                .route(context -> {
                            reference.set(context.request().loggedInUserOrThrow());
                            context.result(Message.of("Perfect!", 200));
                        }
                )
                .build());

        final var authenticationResponse = sendGetRequest(
                elepy.url() + "/random-secured-route",
                "Bearer a_wrong_token"
        );

        assertThat(authenticationResponse.statusCode())
                .isEqualTo(401);
    }

    @Test
    void cannot_AccessSecuredRoute_withoutToken() throws IOException, InterruptedException {
        var reference = new AtomicReference<User>();

        elepy.addRoute(RouteBuilder.anElepyRoute()
                .permissions("authenticated")
                .method(HttpMethod.GET)
                .path("/random-secured-route")
                .route(context -> {
                            reference.set(context.request().loggedInUserOrThrow());
                            context.result(Message.of("Perfect!", 200));
                        }
                )
                .build());

        final var authenticationResponse = sendGetRequest(
                elepy.url() + "/random-secured-route",
                null
        );

        assertThat(authenticationResponse.statusCode())
                .isEqualTo(401);
    }

    @Test
    void can_AccessSelf() throws IOException, InterruptedException {

        final var user1 = new CustomUser();
        user1.setId("user");
        user1.setUsername("user");
        user1.setEmail("ryansemail@live.com");
        user1.setPassword("userPassword");

        // Create user
        sendPostRequest(
                elepy.url() + "/users",
                objectMapper.writeValueAsString(user1),
                "application/json",
                createBasicAuthHeader("user", "user") // Assuming Elepy's /users endpoint for creation also uses basic auth
        );


        // Access logged-in user info
        final var response = sendGetRequest(
                elepy.url() + "/elepy/logged-in-user",
                createBasicAuthHeader("user", "userPassword")
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        final JsonNode responseBody = objectMapper.readTree(response.body());
        assertThat(responseBody.get("email").asText())
                .isEqualTo("ryansemail@live.com");
    }
}