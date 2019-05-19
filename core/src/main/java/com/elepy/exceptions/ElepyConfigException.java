package com.elepy.exceptions;

public class ElepyConfigException extends ElepyException {
    public ElepyConfigException(String message) {
        super(message, 500);
    }

    public ElepyConfigException(String message, Throwable cause) {
        super(message, 500, cause);
    }
}
