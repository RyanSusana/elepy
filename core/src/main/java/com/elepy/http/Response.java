package com.elepy.http;

import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;

import javax.servlet.http.HttpServletResponse;

public interface Response {
    void status(int statusCode);

    int status();

    void result(String body);

    String result();

    HttpServletResponse servletResponse();

    void type(String type);

    String type();

    void removeCookie(String name);

    void cookie(String name, String value);

    void cookie(String name, String value, int maxAge);

    void redirect(String location);

    void redirect(String location, int httpStatusCode);

    default void result(Message message) {
        result(String.format("{\"status\":%d,\"message\":\"%s\"}", message.getStatus(), message.getMessage()), message.getStatus());
        type("application/json");
    }

    default void result(String message, int status) {
        result(message);
        status(status);
    }

    default void terminateWithResult(String message, int status) {
        throw new ElepyException(message, status);
    }

    default void terminateWithResult(Message message) {
        terminateWithResult(message.getMessage(), message.getStatus());
    }
}
