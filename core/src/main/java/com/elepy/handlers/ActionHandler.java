package com.elepy.handlers;

public interface ActionHandler<T> {

    void handle(HandlerContext<T> ctx) throws Exception;

}
