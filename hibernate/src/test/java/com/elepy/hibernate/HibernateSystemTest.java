package com.elepy.hibernate;

import com.elepy.Elepy;
import com.elepy.tests.SystemTest;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HibernateSystemTest extends SystemTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(HibernateConfiguration.inMemory());
    }
}
