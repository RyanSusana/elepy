package com.elepy.dao.jongo;

import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.id.HexIdentityProvider;
import com.elepy.utils.ClassUtils;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ElepyIdUpdater implements org.jongo.ObjectIdUpdater {

    private final Crud crud;
    private final IdentityProvider identityProvider;

    public ElepyIdUpdater(Crud crud, IdentityProvider identityProvider) {
        this.crud = crud;
        this.identityProvider = identityProvider;
    }


    @Override
    public boolean mustGenerateObjectId(Object pojo) {
        final Optional<String> id = ClassUtils.getId(pojo);

        return id.map(s -> s.trim().equals("")).orElse(true);
    }

    @Override
    public Object getId(Object pojo) {
        return ClassUtils.getId(pojo).orElseThrow(() -> new ElepyException("No ID found"));
    }

    @Override
    public void setObjectId(Object target, ObjectId id) {

        final Field idField = ClassUtils.getIdField(target.getClass()).orElseThrow(() -> new ElepyException("No ID field found"));

        idField.setAccessible(true);
        try {
            idField.set(target, provider(target).getId(target, crud));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private IdentityProvider provider(Object item) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        if (identityProvider != null) {
            return identityProvider;
        }
        ClassUtils.getIdField(item.getClass()).orElseThrow(() -> new ElepyException("No ID found"));
        final com.elepy.annotations.IdProvider annotation = item.getClass().getAnnotation(com.elepy.annotations.IdProvider.class);

        if (annotation != null) {
            final Optional<Constructor> o = ClassUtils.getEmptyConstructor(annotation.value());
            if (!o.isPresent()) {
                throw new IllegalStateException(annotation.value() + " has no empty constructor.");
            }
            return ((Constructor<IdentityProvider>) o.get()).newInstance();

        } else {
            return new HexIdentityProvider<>();
        }

    }
}
