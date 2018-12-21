package com.elepy.concepts;

import com.elepy.dao.Crud;

import java.util.Random;

public interface IdentityProvider<T> {

    Random random = new Random();
    String getId(T item, Crud<T> dao);

    default  String getRandomHexString(int numchars) {
        if(numchars <= 0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }

}
