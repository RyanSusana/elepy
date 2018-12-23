package com.elepy.exceptions;

public class ElepyException extends ElepyErrorMessage {
    public ElepyException(String message) {
        super(message, 400);
    }
}
