package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicFuntionalityTest;
import com.elepy.tests.config.DatabaseConfigurations;

public class MongoBasicFuntionalityTest extends BasicFuntionalityTest {
    public MongoBasicFuntionalityTest() {
        super(DatabaseConfigurations.MongoDB);
    }
}
