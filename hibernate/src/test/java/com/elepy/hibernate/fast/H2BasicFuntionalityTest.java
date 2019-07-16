package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFuntionalityTest;

public class H2BasicFuntionalityTest extends BasicFuntionalityTest {
    public H2BasicFuntionalityTest() {
        super(DatabaseConfigurations.H2);
    }
}
