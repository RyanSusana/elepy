package com.elepy.vertx;

import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import io.vertx.ext.web.RoutingContext;

public class VertxContext implements HttpContext {

    private final VertxRequest request;
    private final VertxResponse response;

    public VertxContext(RoutingContext routingContext) {
        this(new VertxRequest(routingContext), new VertxResponse(routingContext));
    }

    public VertxContext(VertxRequest request, VertxResponse response) {
        this.request = request;
        this.response = response;
    }


    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response response() {
        return response;
    }
}
