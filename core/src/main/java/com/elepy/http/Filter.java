package com.elepy.http;

@FunctionalInterface
public interface Filter {
    void authenticate(HttpContext context) throws Exception;
}
