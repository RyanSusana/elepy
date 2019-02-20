package com.elepy.id;

import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.utils.StringUtils;

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
            throw new ElepyConfigException("Can't create a HexIdentityProvider with a minimum length of less than 2");
        }
    }


    @Override
    public String getId(Object item, Crud dao) {
        String id = generateOne();
        if (dao.getById(id).isPresent()) {
            return getId(item, dao);
        }
        return id;
    }

    private String generateOne() {
        String generation = prefix + StringUtils.getRandomHexString(length);

        if (allCaps) {
            generation = generation.toUpperCase();
        }
        return generation;
    }
}
