package com.elepy.models;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public enum NumberType {
    INTEGER(new Class[]{long.class, int.class, short.class, byte.class, BigInteger.class, Byte.class, Integer.class, Long.class, Short.class}),
    DECIMAL(new Class[]{float.class, double.class, Double.class, BigDecimal.class, Float.class});

    private final List<Class<? extends Number>> availableClasses;

    NumberType(Class<? extends Number>[] availableClasses) {
        this.availableClasses = Arrays.asList(availableClasses);
    }

    public static NumberType guessType(Field field) {
        return guessType(field.getType());
    }

    public static NumberType guessType(Class<?> field) {
        for (NumberType numberType : NumberType.values()) {
            for (Class<? extends Number> availableClass : numberType.availableClasses) {
                if (field.equals(availableClass)) {
                    return numberType;
                }
            }
        }
        return null;
    }
}
