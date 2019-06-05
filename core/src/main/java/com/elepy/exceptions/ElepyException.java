package com.elepy.exceptions;

public class ElepyException extends ElepyErrorMessage {
    public ElepyException(String message) {
        this(message, 400);
    }

    public ElepyException(String message, int status) {
        super(message, status);
    }

    public ElepyException(String message, int status, Throwable cause) {
        super(message, status, cause);
    }

    public static ElepyException of(String message, int status, Throwable cause) {
        return new ElepyException(message, status, cause);
    }

    public static ElepyException of(String message, int status) {
        return new ElepyException(message, status);
    }

    public static ElepyException of(String message) {
        return new ElepyException(message);
    }
}
