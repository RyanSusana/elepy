package com.ryansusana.elepy.models;

public class RestErrorMessage extends RuntimeException {
    public RestErrorMessage(String message) {
        super(message);
    }
}
