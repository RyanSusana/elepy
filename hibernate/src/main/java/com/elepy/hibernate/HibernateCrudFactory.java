package com.elepy.hibernate;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.di.ElepyContext;
import com.elepy.schemas.Schema;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

public class HibernateCrudFactory implements CrudFactory {

    @Inject
    private ElepyContext elepyContext;

    @Override
    public <T> Crud<T> crudFor(Schema<T> type) {

        final SessionFactory singleton = elepyContext.getDependency(SessionFactory.class);
        return new HibernateDao<>(singleton, elepyContext.objectMapper(), type);
    }
}
