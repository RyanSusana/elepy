package com.elepy.test.e2e.user;

import com.elepy.Configuration;
import com.elepy.hibernate.HibernateConfiguration;

import java.util.Properties;

public class HibernateUserEndToEndTest extends ElepyUserEndToEndTest {
    @Override
    public Configuration configuration() {


        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:h2:mem:testDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        properties.setProperty("hibernate.connection.username", "SA");
        properties.setProperty("hibernate.connection.password", "");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create");


        return HibernateConfiguration.of(new org.hibernate.cfg.Configuration().setProperties(properties));

    }

}
