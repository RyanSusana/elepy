package com.elepy.http;

public interface Filter extends HttpContextHandler {
    @Override
    void handle(HttpContext context) throws Exception;
}
