package com.elepy.http;

public interface Filter {
    void authenticate(HttpContext context) throws Exception;
}
