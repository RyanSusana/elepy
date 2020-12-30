package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Translated {

    @JsonProperty
    private final String messageTemplate;

    private final Object input;

    private Translated(String messageTemplate, Object input) {
        this.messageTemplate = messageTemplate;
        this.input = input;
    }

    public static Translated of(String message, Object... input) {
        return new Translated(message, input);
    }
    public static Translated of(String message) {
        return Translated.of(message);
    }

    public static Translated of(String message, Map<String, Object> input) {
        return new Translated(message, input);
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }


    public Object getInput() {
        return input;
    }
}
