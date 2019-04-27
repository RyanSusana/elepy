package com.elepy.test.e2e.user.slow;

import com.elepy.Configuration;
import com.elepy.database.DatabaseConfigurations;
import com.elepy.test.e2e.user.ElepyUserEndToEndTest;

public class MSSQLUserEndToEndTest extends ElepyUserEndToEndTest {
    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.MSSQL;
    }
}
