package com.elepy.exceptions;

public final class ErrorMessageBuilder {
    private String message;
    private int status;

    private ErrorMessageBuilder() {
    }

    public static ErrorMessageBuilder anElepyException() {
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

    public ElepyException build() {
        return ElepyException.translated(message, status);
    }
}
