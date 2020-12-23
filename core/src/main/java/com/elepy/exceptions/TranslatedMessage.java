package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslatedMessage {


    @JsonProperty
    private final String messageTemplate;

    private final Object[] input;

    public TranslatedMessage(String messageTemplate, Object... input) {
        this.messageTemplate = messageTemplate;
        this.input = input;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }


    public Object[] getInput() {
        return input;
    }
}
