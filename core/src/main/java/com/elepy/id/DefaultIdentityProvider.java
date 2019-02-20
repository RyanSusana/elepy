package com.elepy.id;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;

/**
 * This is the default {@link IdentityProvider} it delegates to {@link HexIdentityProvider} for Strings and {@link NumberIdentityProvider} for numbers.
 *
 * @param <T> The type of the Model
 */
public class DefaultIdentityProvider<T> implements IdentityProvider<T> {

    private final HexIdentityProvider<T> hexIdentityProvider = new HexIdentityProvider<>();
    private final NumberIdentityProvider<T> numberIdentityProvider = new NumberIdentityProvider<>();

    @Override
    public void provideId(T item, Crud<T> dao) {
        Class<?> idType = ClassUtils.getIdField(item.getClass()).orElseThrow(() -> new ElepyException("Can't find the ID field", 500)).getType();

        if (idType == String.class) {
            hexIdentityProvider.provideId(item, dao);
        } else {
            numberIdentityProvider.provideId(item, dao, idType);
        }
    }
}
