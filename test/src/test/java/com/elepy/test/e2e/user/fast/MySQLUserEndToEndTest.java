package com.elepy.test.e2e.user.fast;

import com.elepy.Configuration;
import com.elepy.database.DatabaseConfigurations;
import com.elepy.test.e2e.user.ElepyUserEndToEndTest;


public class MySQLUserEndToEndTest extends ElepyUserEndToEndTest {
    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.MySQL;
    }
}
