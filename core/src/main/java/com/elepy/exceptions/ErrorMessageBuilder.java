package com.elepy.exceptions;

public final class ErrorMessageBuilder {
    private String message;
    private int status;

    private ErrorMessageBuilder() {
    }

    public static ErrorMessageBuilder anElepyErrorMessage() {
        return new ErrorMessageBuilder();
    }

    public ErrorMessageBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorMessageBuilder withStatus(int status) {
        this.status = status;
        return this;
    }

    public ElepyErrorMessage build() {
        return new ElepyErrorMessage(message, status);
    }
}
