package com.elepy.id;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ClassUtils;
import com.elepy.utils.StringUtils;

import java.lang.reflect.Field;

public class HexIdentityProvider<T> implements IdentityProvider<T> {


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
            throw new ElepyConfigException("Can't singleCreate a HexIdentityProvider with a minimum length of less than 2");
        }
    }


    @Override
    public void provideId(T item, Crud<T> dao) {


        String currentId = (String) ClassUtils.getId(item).orElse("");


        if (currentId.isEmpty()) {
            String id = generateId(dao);

            Field field = ClassUtils.getIdField(dao.getType()).orElseThrow(() -> new ElepyException("No ID field", 500));

            field.setAccessible(true);

            try {
                field.set(item, id);
            } catch (IllegalAccessException e) {
                throw new ElepyException("Can't reflectively access ID field: " + field.getName(), 500);
            }
        }

    }

    private String generateId(Crud<T> dao) {
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
