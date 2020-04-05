package com.elepy.http;

import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public interface Response {
    ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    void status(int statusCode);

    int status();

    void result(String body);

    void result(byte[] bytes);

    default void result(InputStream stream) {
        try {
            result(IOUtils.toByteArray(stream));
        } catch (IOException e) {
            throw new ElepyException("Error processing InputStream", 500, e);
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
            throw new ElepyException("Error writing json", 500);
        }
    }

    default void json(Object object) {
        try {
            type("application/json");
            result(DEFAULT_MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ElepyException("Failed to parse json.");
        }
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

    void header(String s, String s1);
}
