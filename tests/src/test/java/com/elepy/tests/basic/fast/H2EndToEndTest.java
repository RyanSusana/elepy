package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;

public class H2EndToEndTest extends BasicEndToEndTest {
    public H2EndToEndTest() {
        super(DatabaseConfigurations.H2);
    }
}
