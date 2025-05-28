package com.elepy.hibernate.fast;

import com.elepy.Elepy;
import com.elepy.annotations.Tag;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.sparkjava.SparkService;
import com.elepy.tests.basic.BasicFunctionalityTest;
import org.junit.jupiter.api.Disabled;

@Tag("slow")
@Disabled("Invalid MySQL setup")
public class MySQLBasicFunctionalityTest extends BasicFunctionalityTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.withHttpService(SparkService.class);
        elepy.addConfiguration(DatabaseConfigurations.MySQL5);
    }
}
