package com.elepy.tests;

import com.elepy.Elepy;
import com.elepy.auth.users.User;
import com.elepy.auth.users.UserService;
import com.elepy.crud.Crud;
import com.elepy.http.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ElepySystemUnderTest extends Elepy implements HttpService {
    private final String url;
    private final int port;

    private final List<ElepyConfigHelper> elepyConfigHelpers = new ArrayList<>();

    private static int counter = 3117;

    private ElepySystemUnderTest(int port) {
        super();
        this.withPort(port);
        this.port = port;
        url = String.format("http://localhost:%d", port);
    }


    public static ElepySystemUnderTest create() {
        return new ElepySystemUnderTest(++counter);
    }

    public ElepySystemUnderTest addToStack(Collection<ElepyConfigHelper> stackItem){
        this.elepyConfigHelpers.addAll(stackItem);
        return this;
    }

    @Override
    public void start(){
        elepyConfigHelpers.forEach(elepyConfigHelper -> elepyConfigHelper.configureElepy(this));
        super.start();
    }

    @Override
    public void stop() {
        elepyConfigHelpers.forEach(ElepyConfigHelper::teardown);
        super.stop();
    }


    public String url() {
        return url;
    }


    @Override
    public void port(int port) {
        this.http().port(port);
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public void addRoute(Route route) {
        this.http().addRoute(route);
    }

    @Override
    public void ignite() {
        this.http().ignite();
    }

    @Override
    public void staticFiles(String path, StaticFileLocation location) {
        this.http().staticFiles(path, location);
    }

    @Override
    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        this.http().exception(exceptionClass, handler);
    }

    @Override
    public void before(HttpContextHandler contextHandler) {
        this.http().before(contextHandler);
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        this.http().after(contextHandler);
    }

    @Override
    public String toString() {
        return url();
    }

    public void createInitialUserViaHttp(String username, String password) throws IOException, InterruptedException {
        User user = new User("admin", username, password);
        var userCrud = this.getCrudFor(User.class);

        final HttpResponse<String> response = sendPostRequest(
                url + "/users",
                json(user),
                "application/json",
                null
        );
        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        assertThat(userCrud.count()).isEqualTo(1);
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
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }



    private String json(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
