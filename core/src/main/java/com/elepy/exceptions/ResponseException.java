package com.elepy.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.elepy.concepts.ResponseType;

import java.util.ArrayList;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseException extends RuntimeException {

    private final String message;

    private final Map<String, Object> properties;

    private final ResponseType type;


    public ResponseException(String message, Map<String, Object> properties, ResponseType type) {
        this.message = message;
        this.properties = properties;
        this.type = type;
    }

    public static ResponseExceptionBuilder builder() {
        return new ResponseExceptionBuilder();
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public ResponseType getType() {
        return this.type;
    }

    public static class ResponseExceptionBuilder {
        private String message;
        private ArrayList<String> properties$key;
        private ArrayList<Object> properties$value;
        private ResponseType type;

        ResponseExceptionBuilder() {
        }

        public ResponseException.ResponseExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ResponseException.ResponseExceptionBuilder property(String propertyKey, Object propertyValue) {
            if (this.properties$key == null) {
                this.properties$key = new ArrayList<String>();
                this.properties$value = new ArrayList<Object>();
            }
            this.properties$key.add(propertyKey);
            this.properties$value.add(propertyValue);
            return this;
        }

        public ResponseException.ResponseExceptionBuilder properties(Map<? extends String, ?> properties) {
            if (this.properties$key == null) {
                this.properties$key = new ArrayList<String>();
                this.properties$value = new ArrayList<Object>();
            }
            for (final Map.Entry<? extends String, ?> $lombokEntry : properties.entrySet()) {
                this.properties$key.add($lombokEntry.getKey());
                this.properties$value.add($lombokEntry.getValue());
            }
            return this;
        }

        public ResponseException.ResponseExceptionBuilder clearProperties() {
            if (this.properties$key != null) {
                this.properties$key.clear();
                this.properties$value.clear();
            }

            return this;
        }

        public ResponseException.ResponseExceptionBuilder type(ResponseType type) {
            this.type = type;
            return this;
        }

        public ResponseException build() {
            Map<String, Object> properties;
            switch (this.properties$key == null ? 0 : this.properties$key.size()) {
                case 0:
                    properties = java.util.Collections.emptyMap();
                    break;
                case 1:
                    properties = java.util.Collections.singletonMap(this.properties$key.get(0), this.properties$value.get(0));
                    break;
                default:
                    properties = new java.util.LinkedHashMap<String, Object>(this.properties$key.size() < 1073741824 ? 1 + this.properties$key.size() + (this.properties$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.properties$key.size(); $i++)
                        properties.put(this.properties$key.get($i), this.properties$value.get($i));
                    properties = java.util.Collections.unmodifiableMap(properties);
            }

            return new ResponseException(message, properties, type);
        }

        public String toString() {
            return "ResponseException.ResponseExceptionBuilder(message=" + this.message + ", properties$key=" + this.properties$key + ", properties$value=" + this.properties$value + ", type=" + this.type + ")";
        }
    }
}
