package com.elepy.routes;


import spark.Request;

import java.util.Optional;

/**
 * This is a class meant to provide helper functionality to Elepy
 */
public interface HandlerHelper {

    /**
     * Tries to get the model ID from a request.
     *
     * @param request The request
     * @return an optional ID
     */
    default Optional<String> getModelIdFromRequest(Request request) {
        return Optional.ofNullable(request.params("id"));
    }

}
