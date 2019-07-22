package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;


public class DerbyBasicFunctionalityTest extends BasicFunctionalityTest {
    public DerbyBasicFunctionalityTest() {
        super(DatabaseConfigurations.ApacheDerby);
    }

}
