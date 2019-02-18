package com.elepy.routes;


import com.elepy.http.Request;

import java.util.Optional;

/**
 * This is a class meant to provide helper functionality to Elepy
 */
public interface HandlerHelper {

    /**
     * Tries to GET the model ID from a request.
     *
     * @param request The request
     * @return an optional ID
     */
    default Optional<String> getModelIdFromRequest(Request request) {
        return Optional.ofNullable(request.params("id"));
    }

}
