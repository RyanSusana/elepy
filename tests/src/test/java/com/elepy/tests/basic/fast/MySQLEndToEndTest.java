package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;


public class MySQLEndToEndTest extends BasicEndToEndTest {

    public MySQLEndToEndTest() {
        super(DatabaseConfigurations.MySQL5);
    }
}
