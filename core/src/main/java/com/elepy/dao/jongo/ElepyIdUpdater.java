package com.elepy.dao.jongo;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.id.DefaultIdentityProvider;
import com.elepy.id.IdentityProvider;
import com.elepy.utils.ClassUtils;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ElepyIdUpdater implements org.jongo.ObjectIdUpdater {

    private final Crud crud;
    private IdentityProvider identityProvider;

    public ElepyIdUpdater(Crud crud) {
        this.crud = crud;
    }


    @Override
    public boolean mustGenerateObjectId(Object pojo) {
        return ClassUtils.getId(pojo).map(o -> o instanceof String && ((String) o).trim().isEmpty()).orElse(true);

    }

    @Override
    public Object getId(Object pojo) {
        return ClassUtils.getId(pojo).orElseThrow(() -> new ElepyException("No ID found"));
    }

    @Override
    public void setObjectId(Object target, ObjectId id) {
        try {
            provider(target).provideId(target, crud);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private IdentityProvider provider(Object item) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (identityProvider == null) {
            ClassUtils.getIdField(item.getClass()).orElseThrow(() -> new ElepyException("No ID found"));
            final com.elepy.annotations.IdProvider annotation = item.getClass().getAnnotation(com.elepy.annotations.IdProvider.class);

            if (annotation != null) {
                final Optional<Constructor<?>> o = ClassUtils.getEmptyConstructor(annotation.value());
                if (!o.isPresent()) {
                    throw new IllegalStateException(annotation.value() + " has no empty constructor.");
                }
                return ((Constructor<IdentityProvider>) o.get()).newInstance();

            } else {
                return new DefaultIdentityProvider();
            }
        }
        return identityProvider;

    }
}
