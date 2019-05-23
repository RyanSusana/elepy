package com.elepy.utils;

import com.github.slugify.Slugify;

import java.util.Random;

public class StringUtils {

    private static Random random = new Random();
    private static final Slugify slugify = new Slugify();

    private StringUtils() {

    }

    public static String slugify(String toSlugify) {
        return slugify.slugify(toSlugify);
    }

    public static String getOrDefault(String toGet, String defaultValue) {
        if (isEmpty(toGet)) {
            return defaultValue;
        }
        return toGet;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isBlank();
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

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
