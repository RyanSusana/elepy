package com.elepy.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpAction {
    private final String name;
    private final String slug;
    private final AccessLevel level;
    private final HttpMethod method;
    private final ActionType actionType;

    @JsonCreator
    public HttpAction(@JsonProperty("name") String name,
                      @JsonProperty("slug") String slug,
                      @JsonProperty("accessLevel") AccessLevel level,
                      @JsonProperty("method") HttpMethod method,
                      @JsonProperty("type") ActionType actionType) {
        this.name = name;
        this.slug = slug;
        this.level = level;
        this.method = method;
        this.actionType = actionType;
    }

    public static HttpAction of(@JsonProperty("name") String name,
                                @JsonProperty("slug") String slug,
                                @JsonProperty("accessLevel") AccessLevel level,
                                @JsonProperty("method") HttpMethod method,
                                @JsonProperty("type") ActionType actionType) {
        return new HttpAction(name, slug, level, method, actionType);
    }

    public String getSlug() {
        return slug;
    }

    public AccessLevel getLevel() {
        return level;
    }


    public HttpMethod getMethod() {
        return method;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getName() {
        return name;
    }
}
