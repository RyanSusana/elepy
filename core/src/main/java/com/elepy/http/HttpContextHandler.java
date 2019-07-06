package com.elepy.http;

import com.elepy.exceptions.ElepyException;

@FunctionalInterface
public interface HttpContextHandler {

    void handle(HttpContext context) throws Exception;

    default void handleWithExceptions(HttpContext context) {
        try {
            handle(context);
        } catch (ElepyException e) {
            throw e;
        } catch (Exception e) {
            throw new ElepyException(e.getMessage(), 500, e);
        }
    }
}
