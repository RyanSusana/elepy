package com.elepy.hibernate;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.di.ElepyContext;
import com.elepy.schemas.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

@ApplicationScoped
public class HibernateCrudFactory implements CrudFactory {

    private ObjectMapper objectMapper;
    private SessionFactoryProvider sessionFactoryProvider;

    @Inject
    public HibernateCrudFactory(SessionFactoryProvider sessionFactoryProvider, ObjectMapper objectMapper) {
        this.sessionFactoryProvider = sessionFactoryProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> Crud<T> crudFor(Schema<T> type) {

        final SessionFactory singleton = sessionFactoryProvider.getSessionFactory();
        return new HibernateDao<>(singleton, objectMapper, type);
    }
}
