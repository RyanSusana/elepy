package com.elepy.utils;

import java.util.Random;

public class StringUtils {

    private static Random random = new Random();

    private StringUtils() {

    }

    public static String getRandomHexString(int numchars) {
        if (numchars <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }
}
