package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;


public class MySQLBasicFunctionalityTest extends BasicFunctionalityTest {

    public MySQLBasicFunctionalityTest() {
        super(DatabaseConfigurations.MySQL5);
    }
}
