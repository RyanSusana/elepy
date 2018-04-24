package com.ryansusana.elepy.concepts;

import org.jongo.marshall.jackson.oid.MongoId;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.UUID;

public class IdProvider {
    public static String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    //Never returns null because all RestModels must be annotated with MongoId
    public static Field getIdField(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(MongoId.class))
                return field;
        }
        return null;
    }
}
