package com.elepy.tests;

import com.elepy.Elepy;
import com.elepy.auth.User;
import com.elepy.http.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElepySystemUnderTest extends Elepy implements HttpService {
    private final String url;
    private final int port;


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
    public void before(String path, HttpContextHandler contextHandler) {
        this.http().before(path, contextHandler);
    }

    @Override
    public void after(String path, HttpContextHandler contextHandler) {
        this.http().after(path, contextHandler);
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        this.http().after(contextHandler);
    }

    @Override
    public String toString() {
        return url();
    }

    public void createInitialUserViaHttp(String username, String password) throws UnirestException {
        User user = new User("admin", username, password, Collections.emptyList());

        var userUrl = url + "/users";
        final HttpResponse<String> response = Unirest
                .post(userUrl)
                .body(json(user))
                .asString();

        assertEquals(200, response.getStatus());
    }

    private String json(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
