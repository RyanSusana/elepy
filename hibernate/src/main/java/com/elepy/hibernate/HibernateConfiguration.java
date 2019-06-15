package com.elepy.hibernate;

import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfiguration implements com.elepy.Configuration {

    private final Configuration hibernateConfiguration;

    public HibernateConfiguration(Configuration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }

    public static HibernateConfiguration of(Configuration c) {
        return new HibernateConfiguration(c);
    }

    @Override
    public void before(ElepyPreConfiguration elepy) {
        for (Class<?> model : elepy.getModels()) {
            hibernateConfiguration.addAnnotatedClass(model);
        }


        SessionFactory sessionFactory = hibernateConfiguration.buildSessionFactory();

        elepy.registerDependency(SessionFactory.class, sessionFactory);
        elepy.withDefaultCrudProvider(HibernateCrudFactory.class);
    }

    @Override
    public void after(ElepyPostConfiguration elepy) {

    }
}
