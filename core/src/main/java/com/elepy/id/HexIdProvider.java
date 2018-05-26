package com.elepy.id;

import com.elepy.concepts.IdProvider;
import com.elepy.dao.Crud;

import java.util.Random;

public class HexIdProvider extends IdProvider {


    private final boolean allCaps;

    private final int length;
    private final String prefix;

    public HexIdProvider() {
        this("", false, 10);
    }

    public HexIdProvider(String prefix, boolean allCaps, int length) {
        this.allCaps = allCaps;
        this.length = length;
        this.prefix = prefix == null ? "" : prefix;
        if (length < 2) {
            throw new IllegalStateException("Can't create a HexIdProvider with a minimum length of less than 2");
        }
    }

    public static String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
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
        String generation = prefix + getRandomHexString(length);

        if (allCaps) {
            generation = generation.toUpperCase();
        }
        return generation;
    }
}
