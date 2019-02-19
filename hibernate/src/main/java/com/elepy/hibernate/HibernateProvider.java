package com.elepy.hibernate;

import com.elepy.annotations.IdProvider;
import com.elepy.annotations.Inject;
import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.dao.CrudProvider;
import com.elepy.di.ElepyContext;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.utils.ClassUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class HibernateProvider implements CrudProvider {
    private static final Logger logger = LoggerFactory.getLogger(HibernateProvider.class);

    @Inject
    private ElepyContext elepyContext;

    @Override
    public <T> Crud<T> crudFor(Class<T> type) {

        final SessionFactory singleton = elepyContext.getDependency(SessionFactory.class);

        final IdProvider annotation = type.getAnnotation(IdProvider.class);
        final IdentityProvider<T> identityProvider;
        if (annotation != null) {
            try {
                final Constructor<? extends IdentityProvider> constructor = ClassUtils.emptyConstructor(annotation.value());
                identityProvider = constructor.newInstance();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new IllegalStateException("Can't create IdentityProvider with annotation IdProvider");
            }
        } else {
            identityProvider = new DefaultIdentityProvider<>();
        }
        return new HibernateDao<>(singleton, identityProvider, elepyContext.getObjectMapper(), type);
    }
}
