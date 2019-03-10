package com.elepy.http;

public class SparkContext implements HttpContext {
    private final SparkRequest request;
    private final SparkResponse response;

    public SparkContext(SparkRequest request, SparkResponse response) {
        this.request = request;
        this.response = response;
    }

    public SparkContext(spark.Request request, spark.Response response) {
        this(new SparkRequest(request), new SparkResponse(response));
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
