package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFuntionalityTest;

public class HSQLBasicFuntionalityTest extends BasicFuntionalityTest {
    public HSQLBasicFuntionalityTest() {
        super(DatabaseConfigurations.HSQL);
    }
}
