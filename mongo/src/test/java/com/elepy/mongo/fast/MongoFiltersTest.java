package com.elepy.mongo.fast;

import com.elepy.Elepy;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.sparkjava.SparkService;
import com.elepy.tests.dao.FiltersTest;

public class MongoFiltersTest extends FiltersTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(MongoConfiguration.inMemory());
        elepy.withHttpService(SparkService.class);
    }
}
