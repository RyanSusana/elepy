package com.elepy.test.e2e.user.fast;

import com.elepy.Configuration;
import com.elepy.database.DatabaseConfigurations;
import com.elepy.test.e2e.user.EndToEndTest;

public class HSQLEndToEndTest extends EndToEndTest {

    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.HSQL;
    }
} 
