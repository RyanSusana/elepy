package com.elepy.models;

import javax.servlet.http.HttpServletResponse;

/**
 * Wrapper around the Spark Response object.
 *
 * @see <a href="http://javadoc.io/doc/com.sparkjava/spark-core/2.8.0">Spark Documentation</a>
 */
public class Response {
    private final spark.Response sparkResponse;

    public Response(spark.Response sparkResponse) {
        this.sparkResponse = sparkResponse;
    }

    public void status(int statusCode) {
        sparkResponse.status(statusCode);
    }

    public int status() {
        return sparkResponse.status();
    }

    public void type(String contentType) {
        sparkResponse.type(contentType);
    }

    public String type() {
        return sparkResponse.type();
    }

    public void body(String body) {
        sparkResponse.body(body);
    }

    public String body() {
        return sparkResponse.body();
    }

    public HttpServletResponse raw() {
        return sparkResponse.raw();
    }

    public void redirect(String location) {
        sparkResponse.redirect(location);
    }

    public void redirect(String location, int httpStatusCode) {
        sparkResponse.redirect(location, httpStatusCode);
    }

    public void header(String header, String value) {
        sparkResponse.header(header, value);
    }

    public void cookie(String name, String value) {
        sparkResponse.cookie(name, value);
    }

    public void cookie(String name, String value, int maxAge) {
        sparkResponse.cookie(name, value, maxAge);
    }

    public void cookie(String name, String value, int maxAge, boolean secured) {
        sparkResponse.cookie(name, value, maxAge, secured);
    }

    public void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        sparkResponse.cookie(name, value, maxAge, secured, httpOnly);
    }

    public void cookie(String path, String name, String value, int maxAge, boolean secured) {
        sparkResponse.cookie(path, name, value, maxAge, secured);
    }

    public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        sparkResponse.cookie(path, name, value, maxAge, secured, httpOnly);
    }

    public void cookie(String domain, String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        sparkResponse.cookie(domain, path, name, value, maxAge, secured, httpOnly);
    }

    public void removeCookie(String name) {
        sparkResponse.removeCookie(name);
    }

    public void removeCookie(String path, String name) {
        sparkResponse.removeCookie(path, name);
    }
}
