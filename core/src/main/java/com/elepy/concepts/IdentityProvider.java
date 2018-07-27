package com.elepy.concepts;

import com.elepy.dao.Crud;

import java.util.Random;

public interface IdentityProvider<T> {
    String getId(T item, Crud<T> dao);

    default  String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }

}
