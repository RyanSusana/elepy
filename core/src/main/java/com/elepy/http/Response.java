package com.elepy.http;

import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public interface Response {

    void status(int statusCode);

    int status();

    <T> T attribute(String attribute);

    void result(String body);

    void result(byte[] bytes);

    default void result(InputStream stream) {
        try {
            result(IOUtils.toByteArray(stream));
        } catch (IOException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    String result();

    void type(String type);

    String type();

    void removeCookie(String name);

    void cookie(String name, String value);

    void cookie(String name, String value, int maxAge);

    default void redirect(String location) {
        redirect(location, 301);
    }

    void redirect(String location, int httpStatusCode);

    default void result(Message message) {
        try {
            final String s = new ObjectMapper().writeValueAsString(message);
            result(s, message.getStatus());
            type("application/json");
        } catch (JsonProcessingException e) {
            throw ElepyException.internalServerError();
        }
    }

    default void json(Object object) {
        try {
            type("application/json");
            result(((ObjectMapper) attribute("objectMapper")).writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw ElepyException.translated("{elepy.messages.exceptions.errorParsingJson}");
        }
    }

    default void result(String message, int status) {
        result(message);
        status(status);
    }

    default void terminateWithResult(Object message, int status) {
        throw new ElepyException(message, status);
    }

    default void terminateWithResult(Message message) {
        terminateWithResult(message.getMessage(), message.getStatus());
    }

    void header(String s, String s1);
}
