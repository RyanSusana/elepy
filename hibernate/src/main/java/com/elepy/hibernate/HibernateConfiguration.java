package com.elepy.hibernate;

import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateConfiguration implements com.elepy.Configuration {

    private final Configuration hibernateConfiguration;

    public HibernateConfiguration(Configuration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }

    public static HibernateConfiguration of(Configuration c) {
        return new HibernateConfiguration(c);
    }

    public static HibernateConfiguration fromEnv(String hibernateCfgXml) {
        return of(new Configuration().configure(hibernateCfgXml));
    }

    public static HibernateConfiguration fromEnv() {
        return of(new Configuration().configure("hibernate.cfg.xml"));
    }

    public static HibernateConfiguration inMemory() {
        return createInMemoryHibernateConfig(
                "org.h2.Driver",
                "jdbc:h2:mem:testDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "org.hibernate.dialect.H2Dialect",
                "SA",
                ""
        );
    }

    @Override
    public void before(ElepyPreConfiguration elepy) {
        for (Class<?> model : elepy.getModels()) {
            hibernateConfiguration.addAnnotatedClass(model);
        }


        SessionFactory sessionFactory = hibernateConfiguration.buildSessionFactory();

        elepy.registerDependency(SessionFactory.class, sessionFactory);
        elepy.withDefaultCrudFactory(HibernateCrudFactory.class);
    }

    @Override
    public void after(ElepyPostConfiguration elepy) {

    }

    static HibernateConfiguration createInMemoryHibernateConfig(String driverClassName, String url, String dialect, String username, String password) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", driverClassName);
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.hbm2ddl.auto", "create");

        return HibernateConfiguration.of(new org.hibernate.cfg.Configuration().setProperties(properties));

    }
}
