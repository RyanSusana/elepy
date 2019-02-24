package com.elepy.hibernate;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.di.ElepyContext;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateProvider implements CrudProvider {
    private static final Logger logger = LoggerFactory.getLogger(HibernateProvider.class);

    @Inject
    private ElepyContext elepyContext;

    @Override
    public <T> Crud<T> crudFor(Class<T> type) {

        final SessionFactory singleton = elepyContext.getDependency(SessionFactory.class);
        return new HibernateDao<>(singleton, elepyContext.getObjectMapper(), type);
    }
}
