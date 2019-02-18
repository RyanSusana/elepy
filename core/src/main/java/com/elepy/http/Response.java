package com.elepy.http;

import javax.servlet.http.HttpServletResponse;

public interface Response {
    void status(int statusCode);

    int status();

    void result(String body);

    String result();

    HttpServletResponse raw();

    void type(String type);

    String type();

    void removeCookie(String name);

    void cookie(String name, String value);

    void cookie(String name, String value, int maxAge);

    void redirect(String location);

    void redirect(String location, int httpStatusCode);
}
