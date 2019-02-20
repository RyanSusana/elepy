package com.elepy.id;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;

import java.util.Random;

public class DefaultIdentityProvider<T> implements IdentityProvider<T> {
    private final HexIdentityProvider<T> hexIdentityProvider = new HexIdentityProvider<>();
    private final Random random = new Random();

    @Override
    public Object getId(T item, Crud<T> dao) {
        Class<?> idType = ClassUtils.getIdField(item.getClass()).orElseThrow(() -> new ElepyException("Can't find the ID field", 500)).getType();

        if (idType == String.class) {
            return hexIdentityProvider.getId(item, dao);
        }

        Class wrappedIdType = org.apache.commons.lang3.ClassUtils.primitiveToWrapper(idType);

        Object randomId;
        if (wrappedIdType.equals(Long.class)) {
            randomId = random.nextLong();
        } else {
            randomId = random.nextInt();
        }
        if (dao.getById(randomId).isPresent()) {
            return getId(item, dao);
        } else {
            return randomId;
        }

    }
}
