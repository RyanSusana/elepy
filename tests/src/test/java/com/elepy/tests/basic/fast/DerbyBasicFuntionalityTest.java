package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicFuntionalityTest;
import com.elepy.tests.config.DatabaseConfigurations;


public class DerbyBasicFuntionalityTest extends BasicFuntionalityTest {
    public DerbyBasicFuntionalityTest() {
        super(DatabaseConfigurations.ApacheDerby);
    }

}
