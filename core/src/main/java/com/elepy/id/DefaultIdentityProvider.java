package com.elepy.id;

import com.elepy.crud.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;

/**
 * This is the default {@link IdentityProvider} it delegates to {@link HexIdentityProvider} for Strings and {@link NumberIdentityProvider} for numbers.
 *
 */
public class DefaultIdentityProvider implements IdentityProvider {

    private final HexIdentityProvider hexIdentityProvider = new HexIdentityProvider();
    private final NumberIdentityProvider numberIdentityProvider = new NumberIdentityProvider();

    @Override
    public <T> void provideId(T item, Crud<T> dao) {
        if (ReflectionUtils.getId(item).isPresent()) {
            return;
        }

        Class<?> idType = ReflectionUtils.getIdField(item.getClass()).orElseThrow(() -> ElepyException.internalServerError()).getType();

        if (idType == String.class) {
            hexIdentityProvider.provideId(item, dao);
        } else {
            numberIdentityProvider.provideId(item, dao, idType);
        }
    }
}
