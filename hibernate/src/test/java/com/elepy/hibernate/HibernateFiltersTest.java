package com.elepy.hibernate;

import com.elepy.Elepy;
import com.elepy.tests.dao.FiltersTest;
import org.junit.jupiter.api.Disabled;

public class HibernateFiltersTest extends FiltersTest {
    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(DatabaseConfigurations.H2);
    }

    @Override
    @Disabled("Not yet supported")
    public void canFilter_CONTAINS_onArray() {
        super.canFilter_CONTAINS_onArray();
    }
}
