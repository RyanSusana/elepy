package com.elepy.dao.jongo;

import com.elepy.annotations.RestModel;
import com.elepy.concepts.IdProvider;
import com.elepy.dao.Crud;
import com.elepy.id.HexIdProvider;
import com.elepy.utils.ClassUtils;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ElepyIdUpdater implements org.jongo.ObjectIdUpdater {

    private final Crud crud;

    @Nullable
    private final IdProvider idProvider;

    public ElepyIdUpdater(Crud crud, IdProvider idProvider) {
        this.crud = crud;
        this.idProvider = idProvider;
    }


    @Override
    public boolean mustGenerateObjectId(Object pojo) {
        final Optional<String> id = ClassUtils.getId(pojo);

        return id.map(s -> s.trim().equals("")).orElse(true);
    }

    @Override
    public Object getId(Object pojo) {
        return ClassUtils.getId(pojo).get();
    }

    @Override
    public void setObjectId(Object target, ObjectId id) {

        final Field idField = ClassUtils.getIdField(target.getClass());

        if (idField != null) {
            idField.setAccessible(true);
            try {
                idField.set(target, provider(target).getId(target, crud));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private IdProvider provider(Object item) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        if (idProvider != null) {
            return idProvider;
        }
        final Field idField = ClassUtils.getIdField(item.getClass());
        final RestModel annotation = item.getClass().getAnnotation(RestModel.class);

        if (annotation != null) {
            final Optional<Constructor<?>> o = ClassUtils.getEmptyConstructor(annotation.idProvider());
            if (!o.isPresent()) {
                throw new IllegalStateException(annotation.idProvider() + " has no empty constructor.");
            }
            return ((Constructor<IdProvider>) o.get()).newInstance();

        } else {
            return new HexIdProvider<>();
        }

    }
}
