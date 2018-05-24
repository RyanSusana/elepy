package com.ryansusana.elepy.concepts;

public enum ResponseType {
    CLIENT_ERROR(400), SUCCESSFUL(200), SERVER_ERROR(500), UNAUTHORIZED(401), NOT_FOUND(404), ACCEPTED(202), REDIRECT(301), FOUND(302);

    private final int status;

    ResponseType(int status) {
        this.status = status;
    }

    public boolean isSuccessful() {
        return status >= 200 && status < 300;
    }


    public int getStatus() {
        return this.status;
    }
}
