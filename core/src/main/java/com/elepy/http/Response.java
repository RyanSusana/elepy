package com.elepy.http;

import javax.servlet.http.HttpServletResponse;

public interface Response {
    void status(int statusCode);

    int status();

    void body(String body);

    String body();

    HttpServletResponse raw();

    void type(String type);

    String type();


    void removeCookie(String name);
}
