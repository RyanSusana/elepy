package com.elepy.id;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Random;

/**
 * This {@link IdentityProvider} generates ID's by using random numbers.
 *
 * @param <T> The model type
 */
public class NumberIdentityProvider<T> implements IdentityProvider<T> {
    private final Random random = new Random();

    @Override
    public void provideId(T item, Crud<T> dao) {
        Class<?> idType = ReflectionUtils.getIdField(item.getClass()).orElseThrow(() -> ElepyException.internalServerError()).getType();

        provideId(item, dao, idType);
    }

    public void provideId(T item, Crud<T> dao, Class<?> idType) {
        Field idProperty = ReflectionUtils.getIdField(dao.getType()).orElseThrow(() -> ElepyException.internalServerError());

        idProperty.setAccessible(true);
        try {
            Serializable id = (Serializable) idProperty.get(item);


            long longId = id == null ? -1 : Long.parseLong(id.toString());

            if (longId <= 0 || dao.getById(id).isPresent()) {
                idProperty.set(item, generateId(dao, org.apache.commons.lang3.ClassUtils.primitiveToWrapper(idType)));
            }

        } catch (IllegalAccessException e) {
            throw ElepyException.internalServerError();
        }
    }

    private Object generateId(Crud<T> dao, Class<?> wrappedIdType) {
        Serializable randomId;
        if (wrappedIdType.equals(Long.class)) {
            randomId = Math.abs(random.nextLong());
        } else {
            randomId = Math.abs(random.nextInt());
        }
        if (dao.getById(randomId).isPresent()) {
            return generateId(dao, wrappedIdType);
        }
        return randomId;
    }
}
