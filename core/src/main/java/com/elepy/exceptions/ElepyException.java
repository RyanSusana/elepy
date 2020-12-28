package com.elepy.exceptions;

import com.elepy.annotations.Localized;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ElepyException extends RuntimeException {


    private final Object detail;
    @Localized
    private Object message;

    @JsonProperty
    private final int status;

    @JsonProperty
    private final Map<String, Object> metadata;

    public ElepyException(Object message) {
        this(message, 400);
    }

    public ElepyException(Object message, int status) {
        this(message, status, Map.of(), null);
    }

    public ElepyException(Object message, int status, Map<String, Object> metadata) {
        this(message, status, metadata, null);
    }

    public ElepyException(Object message, int status, Throwable cause) {
        this(message, status, Map.of(), cause);
    }


    public ElepyException(Object message, int status, Map<String, Object> metadata, Throwable cause) {
        super(message.toString(), cause);
        this.message = message;
        this.detail = message;
        this.status = status;
        this.metadata = metadata;
    }

    public static ElepyException of(Object message, int status, Throwable cause) {
        return new ElepyException(message, status, cause);
    }

    public static ElepyException of(Object message, int status) {
        return new ElepyException(message, status);
    }

    public static ElepyException of(Object message) {
        return new ElepyException(message);
    }


    public int getStatus() {
        return status;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Object getTranslatedMessage() {
        return message;
    }
}
