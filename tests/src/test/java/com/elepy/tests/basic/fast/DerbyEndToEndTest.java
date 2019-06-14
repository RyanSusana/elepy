package com.elepy.tests.basic.fast;

import com.elepy.Configuration;
import com.elepy.tests.basic.EndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;


public class DerbyEndToEndTest extends EndToEndTest {
    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.ApacheDerby;
    }
}
