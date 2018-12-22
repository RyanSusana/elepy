package com.elepy.exceptions;

public final class ErrorMessage {
    private String message;
    private int status;

    private ErrorMessage() {
    }

    public static ErrorMessage anElepyErrorMessage() {
        return new ErrorMessage();
    }

    public ErrorMessage withMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorMessage withStatus(int status) {
        this.status = status;
        return this;
    }

    public ElepyErrorMessage build() {
        return new ElepyErrorMessage(message, status);
    }
}
