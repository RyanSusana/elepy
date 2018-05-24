package com.elepy.exceptions;

public class RestErrorMessage extends RuntimeException {
    public RestErrorMessage(String message) {
        super(message);
    }
}
