package com.elepy.http;

@FunctionalInterface
public interface ExceptionHandler<T extends Exception> {
    void handleException(T exception, HttpContext context);
}