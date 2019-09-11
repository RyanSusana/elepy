package com.elepy.vertx;

import com.elepy.http.HttpService;
import com.elepy.http.SparkService;
import com.elepy.tests.http.HttpServiceTest;

public class SparkServiceTest extends HttpServiceTest {
    @Override
    public HttpService httpService() {
        return new SparkService();
    }
}
