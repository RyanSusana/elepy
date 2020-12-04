package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ElepyException extends RuntimeException {

    @JsonProperty
    private final String message;

    @JsonProperty
    private final int status;

    @JsonProperty
    private final Map<String, Object> metadata;

    public ElepyException(String message) {
        this(message, 400);
    }

    public ElepyException(String message, int status) {
        this(message, status, Map.of(), null);
    }

    public ElepyException(String message, int status, Throwable cause) {
        this(message, status, Map.of(), cause);
    }

    public ElepyException(String message, int status, Map<String, Object> metadata, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.status = status;
        this.metadata = metadata;
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


    public int getStatus() {
        return status;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
