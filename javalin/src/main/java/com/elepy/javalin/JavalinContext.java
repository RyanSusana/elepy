package com.elepy.javalin;

import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import io.javalin.http.Context;

public class JavalinContext implements HttpContext {
    private final JavalinRequest request;
    private final JavalinResponse response;

    public JavalinContext(Context context) {
        this.request = new JavalinRequest(context);
        this.response = new JavalinResponse(context);
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
