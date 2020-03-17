package com.elepy.admin;

import com.elepy.http.HttpMethod;
import com.elepy.http.Route;
import com.elepy.http.RouteBuilder;

public class Resource {
    private final String path;
    private final String contentType;
    private final byte[] raw;


    public Resource(String path, String contentType, byte[] raw) {
        this.path = path;
        this.contentType = contentType;
        this.raw = raw;
    }

    public Route toRoute() {
        return RouteBuilder.anElepyRoute().acceptType(contentType).method(HttpMethod.GET).path(path)
                .route(context -> {
                    context.type(contentType);
                    context.response().header("Content-Encoding", "gzip");
                    context.response().result(raw);
                }).build();
    }
}
