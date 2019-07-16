package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFuntionalityTest;


public class MySQLBasicFuntionalityTest extends BasicFuntionalityTest {

    public MySQLBasicFuntionalityTest() {
        super(DatabaseConfigurations.MySQL5);
    }
}
