package com.elepy.http;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SparkResponse implements Response {
    private final spark.Response response;

    public SparkResponse(spark.Response response) {
        this.response = response;
    }

    @Override
    public void status(int statusCode) {
        response.status(statusCode);
    }

    @Override
    public int status() {
        return response.status();
    }

    @Override
    public void type(String contentType) {
        response.type(contentType);
    }

    @Override
    public String type() {
        return response.type();
    }

    @Override
    public void result(String body) {
        response.body(body);
    }

    @Override
    public void result(byte[] bytes) {
        HttpServletResponse raw = response.raw();

        try {
            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String result() {
        return response.body();
    }

    @Override
    public HttpServletResponse servletResponse() {
        return response.raw();
    }

    public void redirect(String location) {
        response.redirect(location);
    }

    public void redirect(String location, int httpStatusCode) {
        response.redirect(location, httpStatusCode);
    }

    public void header(String header, String value) {
        response.header(header, value);
    }

    public void cookie(String name, String value) {
        response.cookie(name, value);
    }

    public void cookie(String name, String value, int maxAge) {
        response.cookie(name, value, maxAge);
    }

    public void cookie(String name, String value, int maxAge, boolean secured) {
        response.cookie(name, value, maxAge, secured);
    }

    public void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        response.cookie(name, value, maxAge, secured, httpOnly);
    }

    public void cookie(String path, String name, String value, int maxAge, boolean secured) {
        response.cookie(path, name, value, maxAge, secured);
    }

    public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        response.cookie(path, name, value, maxAge, secured, httpOnly);
    }

    public void cookie(String domain, String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        response.cookie(domain, path, name, value, maxAge, secured, httpOnly);
    }

    @Override
    public void removeCookie(String name) {
        response.removeCookie(name);
    }

    public void removeCookie(String path, String name) {
        response.removeCookie(path, name);
    }
}
