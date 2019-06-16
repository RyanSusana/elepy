package com.elepy.tests.crud;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.dao.CrudFactory;

public abstract class CrudFactoryTest extends CrudTest {
    public abstract CrudFactory crudFactory();

    @Override
    public Configuration configuration() {
        return new Configuration() {
            @Override
            public void before(ElepyPreConfiguration elepy) {
                elepy.withDefaultCrudFactory(crudFactory());
            }

            @Override
            public void after(ElepyPostConfiguration elepy) {

            }
        };
    }
}
