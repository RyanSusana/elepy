package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;


public class DerbyEndToEndTest extends BasicEndToEndTest {
    public DerbyEndToEndTest() {
        super(DatabaseConfigurations.ApacheDerby);
    }

}
