package com.elepy.javalin;

import com.elepy.http.HttpService;
import com.elepy.tests.http.HttpServiceTest;

public class JavalinServiceTest extends HttpServiceTest {
    @Override
    public HttpService httpService() {
        return new JavalinService();
    }
}
