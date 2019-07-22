package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;

public class HSQLBasicFunctionalityTest extends BasicFunctionalityTest {
    public HSQLBasicFunctionalityTest() {
        super(DatabaseConfigurations.HSQL);
    }
}
