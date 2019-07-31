package com.elepy.mongo;

import com.elepy.Elepy;
import com.elepy.tests.SystemTest;

public class MongoSystemTest extends SystemTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(MongoConfiguration.inMemory());
    }
}
