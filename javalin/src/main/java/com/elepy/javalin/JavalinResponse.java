package com.elepy.javalin;

import com.elepy.http.Response;
import io.javalin.http.Context;

import java.io.ByteArrayInputStream;

public class JavalinResponse implements Response {

    private final Context context;

    public JavalinResponse(Context context) {
        this.context = context;
    }

    @Override
    public void status(int statusCode) {
        context.status(statusCode);
    }

    @Override
    public int status() {
        return context.status();
    }

    @Override
    public void result(String body) {
        context.result(body);
    }

    @Override
    public void result(byte[] bytes) {
        context.result(new ByteArrayInputStream(bytes));
    }

    @Override
    public String result() {
        return context.resultString();
    }

    @Override
    public void type(String type) {
        context.contentType(type);
    }

    @Override
    public String type() {
        return context.contentType();
    }

    @Override
    public void removeCookie(String name) {
        context.removeCookie(name);
    }

    @Override
    public void cookie(String name, String value) {
        context.cookie(name, value);
    }

    @Override
    public void cookie(String name, String value, int maxAge) {
        context.cookie(name, value, maxAge);
    }

    @Override
    public void redirect(String location, int httpStatusCode) {
        context.redirect(location, httpStatusCode);
    }

    @Override
    public void header(String s, String s1) {
        context.header(s, s1);
    }
}
