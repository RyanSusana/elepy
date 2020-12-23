package com.elepy.sparkjava;

import com.elepy.http.Response;
import spark.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

public class SparkResponse implements Response {
    private final Request request;
    private final spark.Response response;

    public SparkResponse(Request request, spark.Response response) {
        this.request = request;
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
    public <T> T attribute(String attribute) {
        return request.attribute(attribute);
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
            OutputStream outputStream = raw.getOutputStream();


            if (Optional.ofNullable(request.headers("Accept-Encoding")).orElse("").contains("gzip") &&
                    Optional.ofNullable(raw.getHeader("Content-Encoding")).orElse("").contains("gzip")) {
                outputStream = new GZIPOutputStream(outputStream, true);
            }
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String result() {
        return response.body();
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
