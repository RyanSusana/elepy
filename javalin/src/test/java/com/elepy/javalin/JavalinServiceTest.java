package com.elepy.javalin;

import com.elepy.http.HttpService;
import com.elepy.tests.http.HttpServiceTest;

import static org.assertj.core.api.Assertions.assertThat;

public class JavalinServiceTest extends HttpServiceTest {
    @Override
    public HttpService httpService() {
        return new JavalinService();
    }
}
