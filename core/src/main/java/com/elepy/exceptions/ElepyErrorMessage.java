package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ElepyErrorMessage extends RuntimeException {

    private final String message;
    private final int status;

    @JsonCreator
    public ElepyErrorMessage(@JsonProperty("message") String message, @JsonProperty("status") int status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
