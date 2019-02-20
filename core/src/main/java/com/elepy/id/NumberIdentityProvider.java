package com.elepy.id;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;

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
        Class<?> idType = ClassUtils.getIdField(item.getClass()).orElseThrow(() -> new ElepyException("Can't find the ID field", 500)).getType();

        provideId(item, dao, idType);
    }

    public void provideId(T item, Crud<T> dao, Class<?> idType) {
        Field idField = ClassUtils.getIdField(dao.getType()).orElseThrow(() -> new ElepyException("No ID field", 500));

        idField.setAccessible(true);
        try {
            Object id = idField.get(item);


            long longId = id == null ? -1 : Long.parseLong(id.toString());

            if (longId <= 0) {
                idField.set(item, generateId(dao, org.apache.commons.lang3.ClassUtils.primitiveToWrapper(idType)));
            }

        } catch (IllegalAccessException e) {
            throw new ElepyException("Failed to reflectively access: " + idField.getName(), 500);
        }
    }

    private Object generateId(Crud<T> dao, Class<?> wrappedIdType) {
        Object randomId;
        if (wrappedIdType.equals(Long.class)) {
            randomId = random.nextLong();
        } else {
            randomId = random.nextInt();
        }
        if (dao.getById(randomId).isPresent()) {
            return generateId(dao, wrappedIdType);
        }
        return randomId;
    }
}
