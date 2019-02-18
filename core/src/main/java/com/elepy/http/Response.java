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
}
