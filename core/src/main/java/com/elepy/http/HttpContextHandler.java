package com.elepy.http;

@FunctionalInterface
public interface HttpContextHandler {

    void handle(HttpContext context) throws Exception;
}
