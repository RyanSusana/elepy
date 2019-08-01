package com.elepy.hibernate;

import com.elepy.Elepy;
import com.elepy.tests.SystemTest;

public class HibernateSystemTest extends SystemTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(HibernateConfiguration.inMemory());
    }
}
