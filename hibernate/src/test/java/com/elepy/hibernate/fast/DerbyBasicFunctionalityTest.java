package com.elepy.hibernate.fast;

import com.elepy.Elepy;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.sparkjava.SparkService;
import com.elepy.tests.basic.BasicFunctionalityTest;
import org.junit.jupiter.api.Tag;


@Tag("slow")
public class DerbyBasicFunctionalityTest extends BasicFunctionalityTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.withHttpService(SparkService.class);
        elepy.addConfiguration(DatabaseConfigurations.ApacheDerby);
    }
}
