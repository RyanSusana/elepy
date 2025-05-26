package com.elepy.id;

import com.elepy.crud.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;
import com.elepy.utils.StringUtils;

import java.lang.reflect.Field;

/**
 * This Identity provider generates a random 10 char hex String for an ID.
 *
 */
public class HexIdentityProvider implements IdentityProvider {


    private final boolean allCaps;

    private final int length;
    private final String prefix;

    public HexIdentityProvider() {
        this("", false, 10);
    }

    public HexIdentityProvider(String prefix, boolean allCaps, int length) {
        this.allCaps = allCaps;
        this.length = length;
        this.prefix = prefix == null ? "" : prefix;
        if (length < 2) {
            throw new ElepyConfigException("Can't create a HexIdentityProvider with a minimum length of less than 2");
        }
    }


    @Override
    public <T> void provideId(T item, Crud<T> dao) {


        String currentId = (String) ReflectionUtils.getId(item).orElse("");


        if (currentId.isEmpty() || dao.getById(currentId).isPresent()) {
            String id = generateId(dao);

            Field field = ReflectionUtils.getIdField(dao.getType()).orElseThrow(ElepyException::internalServerError);

            field.setAccessible(true);

            try {
                field.set(item, id);
            } catch (IllegalAccessException e) {
                throw ElepyException.internalServerError();
            }
        }

    }

    private <T> String generateId(Crud<T> dao) {
        String generation = prefix + StringUtils.getRandomHexString(length);

        if (allCaps) {
            generation = generation.toUpperCase();
        }

        if (dao.getById(generation).isPresent()) {
            return generateId(dao);
        }
        return generation;
    }
}
