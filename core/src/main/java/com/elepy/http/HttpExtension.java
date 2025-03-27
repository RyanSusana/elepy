package com.elepy.http;

public interface HttpExtension {
    public void beforeRequest(Route route, HttpContext httpContext);
    public void afterRequest(Route route, HttpContext httpContext);
}
