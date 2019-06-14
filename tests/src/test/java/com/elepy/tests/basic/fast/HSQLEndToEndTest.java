package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;

public class HSQLEndToEndTest extends BasicEndToEndTest {
    public HSQLEndToEndTest() {
        super(DatabaseConfigurations.HSQL);
    }
}
