package com.elepy.http;

public class SparkContext implements HttpContext {
    private final SparkRequest request;
    private final SparkResponse response;

    public SparkContext(SparkRequest request, SparkResponse response) {
        this.request = request;
        this.response = response;
    }


    @Override
    public Request req() {
        return request;
    }

    @Override
    public Response res() {
        return response;
    }
}
