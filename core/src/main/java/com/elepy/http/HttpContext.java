package com.elepy.http;

public interface HttpContext {

    /**
     * @return The {@link Request} object
     */
    Request request();

    /**
     * @return The {@link Response} object
     */
    Response response();

    /**
     * @return The ID of the model a.k.a request.params("id)
     */
    default Object modelId() {
        return request().modelId();
    }

}


