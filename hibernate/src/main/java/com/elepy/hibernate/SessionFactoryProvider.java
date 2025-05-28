package com.elepy.hibernate;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.SessionFactory;

@ApplicationScoped
public class SessionFactoryProvider {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

}
