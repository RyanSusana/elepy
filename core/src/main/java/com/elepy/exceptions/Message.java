package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private final String message;
    private final int status;


    private final Map<String, Object> properties;

    @JsonCreator
    public Message(@JsonProperty("message") String message, @JsonProperty("status") int status, @JsonProperty("properties") Map<String, Object> properties) {
        this.message = message;
        this.status = status;
        this.properties = properties;
    }

    public static Message of(String message) {
        return of(message, 200);
    }

    public Message withProperty(String key, Object value) {
        getProperties().put(key, value);
        return this;
    }

    public static Message of(String message, int status) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("type", status >= 300 ? MessageType.ERROR : MessageType.MESSAGE);
        return new Message(message, status, properties);
    }

    public static Message redirect(String to) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", MessageType.REDIRECT);

        return new Message(to, 303, properties);
    }

    public static Message htmlContent(String html) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", MessageType.HTML);

        return new Message(html, 200, properties);
    }

    public static Message markdownContent(String markdown) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", MessageType.MARKDOWN);

        return new Message(markdown, 200, properties);
    }

    @JsonProperty
    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
