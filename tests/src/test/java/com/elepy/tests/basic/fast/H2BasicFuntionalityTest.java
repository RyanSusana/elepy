package com.elepy.tests.basic.fast;

import com.elepy.tests.basic.BasicFuntionalityTest;
import com.elepy.tests.config.DatabaseConfigurations;

public class H2BasicFuntionalityTest extends BasicFuntionalityTest {
    public H2BasicFuntionalityTest() {
        super(DatabaseConfigurations.H2);
    }
}
