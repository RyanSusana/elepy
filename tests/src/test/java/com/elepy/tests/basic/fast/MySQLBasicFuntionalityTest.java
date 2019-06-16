package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicFuntionalityTest;
import com.elepy.tests.config.DatabaseConfigurations;


public class MySQLBasicFuntionalityTest extends BasicFuntionalityTest {

    public MySQLBasicFuntionalityTest() {
        super(DatabaseConfigurations.MySQL5);
    }
}
