package com.ryansusana.elepy.exceptions;

public class RestErrorMessage extends RuntimeException {
    public RestErrorMessage(String message) {
        super(message);
    }
}
