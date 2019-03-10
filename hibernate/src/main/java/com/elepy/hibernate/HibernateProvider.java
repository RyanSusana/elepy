package com.elepy.hibernate;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.di.ElepyContext;
import org.hibernate.SessionFactory;

public class HibernateProvider implements CrudProvider {

    @Inject
    private ElepyContext elepyContext;

    @Override
    public <T> Crud<T> crudFor(Class<T> type) {

        final SessionFactory singleton = elepyContext.getDependency(SessionFactory.class);
        return new HibernateDao<>(singleton, elepyContext.getObjectMapper(), type);
    }
}
