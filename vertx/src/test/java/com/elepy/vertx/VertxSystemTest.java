package com.elepy.vertx;

import com.elepy.Elepy;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.tests.SystemTest;

public class VertxSystemTest extends SystemTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.withHttpService(new VertxService());

        elepy.addConfiguration(MongoConfiguration.inMemory());
    }
}
