package com.elepy.http;

@FunctionalInterface
public interface RequestResponseHandler {

    void handle(Request request, Response response) throws Exception;

}
