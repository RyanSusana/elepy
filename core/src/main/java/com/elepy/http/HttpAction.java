package com.elepy.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpAction {
    private final String name;
    private final String path ;
    private final String[] requiredPermissions;
    private final HttpMethod method;
    private final ActionType actionType;

    @JsonCreator
    public HttpAction(@JsonProperty("name") String name,
                      @JsonProperty("path ") String path ,
                      @JsonProperty("requiredPermissions") String[] requiredPermissions,
                      @JsonProperty("method") HttpMethod method,
                      @JsonProperty("type") ActionType actionType) {
        this.name = name;
        this.path = path ;
        this.requiredPermissions = requiredPermissions;
        this.method = method;
        this.actionType = actionType;
    }

    public static HttpAction of(@JsonProperty("name") String name,
                                @JsonProperty("path ") String path ,
                                @JsonProperty("requiredPermissions") String[] requiredPermissions,
                                @JsonProperty("method") HttpMethod method,
                                @JsonProperty("type") ActionType actionType) {
        return new HttpAction(name, path , requiredPermissions, method, actionType);
    }


    public String getPath() {
        return path ;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
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
