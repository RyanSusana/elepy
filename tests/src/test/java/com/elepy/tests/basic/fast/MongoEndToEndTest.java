package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;

public class MongoEndToEndTest extends BasicEndToEndTest {
    public MongoEndToEndTest() {
        super(DatabaseConfigurations.MongoDB);
    }
}
