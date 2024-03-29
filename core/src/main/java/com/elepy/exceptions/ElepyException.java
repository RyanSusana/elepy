package com.elepy.exceptions;

import com.elepy.annotations.Localized;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ElepyException extends RuntimeException {


    private final Object detail;
    @Localized
    private final Object message;

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

    public static ElepyException internalServerError() {
        return internalServerError(null);
    }

    public static ElepyException notAuthorized() {
        return translated(401, "{elepy.messages.exceptions.notAuthorized}");
    }

    public static ElepyException notFound() {
        return notFound(null);
    }

    public static ElepyException notFound(String s) {
        return of(Translated.of("{elepy.messages.exceptions.notFound}", s), 404);
    }

    public static ElepyException internalServerError(Throwable e) {
        return of("{elepy.messages.exceptions.internal}", 500, e);
    }

    public static ElepyException of(Object message, int status, Throwable cause) {
        return new ElepyException(message, status, cause);
    }

    public static ElepyException of(Object message, int status) {
        return new ElepyException(message, status);
    }

    public static ElepyException translated(int status, String message, Object... input) {
        return of(Translated.of(message, input), status);
    }

    public static ElepyException translated(int status, String message, Map<String, Object> input) {
        return of(Translated.of(message, input), status);
    }

    public static ElepyException translated(String message, Object... input) {
        return of(Translated.of(message, input), 400);
    }

    public static ElepyException translated(String message, Map<String, Object> input) {
        return of(Translated.of(message, input), 400);
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
