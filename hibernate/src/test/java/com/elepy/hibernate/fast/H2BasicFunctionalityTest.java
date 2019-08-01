package com.elepy.hibernate.fast;

import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;

public class H2BasicFunctionalityTest extends BasicFunctionalityTest {
    public H2BasicFunctionalityTest() {
        super(DatabaseConfigurations.H2);
    }
}
