package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicFuntionalityTest;
import com.elepy.tests.config.DatabaseConfigurations;

public class HSQLBasicFuntionalityTest extends BasicFuntionalityTest {
    public HSQLBasicFuntionalityTest() {
        super(DatabaseConfigurations.HSQL);
    }
}
