package com.elepy.hibernate;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.di.ElepyContext;
import com.elepy.models.Model;
import org.hibernate.SessionFactory;

public class HibernateCrudFactory implements CrudFactory {

    @Inject
    private ElepyContext elepyContext;

    @Override
    public <T> Crud<T> crudFor(Model<T> type) {

        final SessionFactory singleton = elepyContext.getDependency(SessionFactory.class);
        return new HibernateDao<>(singleton, elepyContext.objectMapper(), type);
    }
}
