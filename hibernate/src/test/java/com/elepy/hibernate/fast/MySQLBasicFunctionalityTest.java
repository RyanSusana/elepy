package com.elepy.hibernate.fast;

import com.elepy.Elepy;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;


public class MySQLBasicFunctionalityTest extends BasicFunctionalityTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(DatabaseConfigurations.MySQL5);
    }
}
