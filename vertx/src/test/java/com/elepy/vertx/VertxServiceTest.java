package com.elepy.vertx;

import com.elepy.http.HttpService;
import com.elepy.tests.http.HttpServiceTest;

public class VertxServiceTest extends HttpServiceTest {

    @Override
    public HttpService httpService() {
        return new VertxService();
    }
}
