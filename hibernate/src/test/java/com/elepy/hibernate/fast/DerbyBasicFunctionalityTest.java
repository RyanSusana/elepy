package com.elepy.hibernate.fast;

import com.elepy.Elepy;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;
import org.junit.jupiter.api.Tag;


@Tag("slow")
public class DerbyBasicFunctionalityTest extends BasicFunctionalityTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(DatabaseConfigurations.ApacheDerby);
    }
}
