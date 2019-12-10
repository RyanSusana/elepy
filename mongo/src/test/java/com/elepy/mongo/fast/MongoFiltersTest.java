package com.elepy.mongo.fast;

import com.elepy.Elepy;
import com.elepy.mongo.MongoConfiguration;
import com.elepy.tests.dao.FiltersTest;
import org.junit.jupiter.api.Disabled;

public class MongoFiltersTest extends FiltersTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(MongoConfiguration.inMemory());
    }

    @Override
    @Disabled("Not yet supported")
    //TODO
    public void canFilter_IS_NULL_onString() {
        super.canFilter_IS_NULL_onString();
    }

    @Override
    @Disabled("Not yet supported")
    //TODO
    public void canFilter_NOT_NULL_onString() {
        super.canFilter_NOT_NULL_onString();
    }
}
