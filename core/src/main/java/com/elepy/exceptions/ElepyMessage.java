package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ElepyMessage {

    private final String message;
    private final int status;

    @JsonCreator
    public ElepyMessage(@JsonProperty("message") String message, @JsonProperty("status") int status) {
        this.message = message;
        this.status = status;
    }

    public ElepyMessage(ElepyErrorMessage elepyErrorMessage) {
        this.status = elepyErrorMessage.getStatus();
        this.message = elepyErrorMessage.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
