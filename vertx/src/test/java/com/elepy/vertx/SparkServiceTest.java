package com.elepy.vertx;

import com.elepy.Elepy;
import com.elepy.http.HttpService;
import com.elepy.http.SparkService;
import com.elepy.tests.http.HttpServiceTest;
import spark.Service;

public class SparkServiceTest extends HttpServiceTest {
    @Override
    public HttpService httpService() {
        return new SparkService(Service.ignite(), new Elepy());
    }
}
