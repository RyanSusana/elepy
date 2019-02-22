package com.elepy.http;

public interface HttpContext {

    Request request();

    Response response();

    default Object modelId() {
        return request().modelId();
    }

}


