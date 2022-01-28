package com.elepy.models.options;

import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;

public class EnumOptions implements Options {

    private final List<Map<String, Object>> availableValues;

    private EnumOptions(List<Map<String, Object>> availableValues) {
        this.availableValues = availableValues;
    }

    public static EnumOptions of(Class<? extends Enum<?>> returnType) {
        return new EnumOptions(MapperUtils.getEnumMapValues(returnType));
    }

    public static EnumOptions of(AnnotatedElement field) {
        final Class<?> returnType = ReflectionUtils.returnTypeOf(field);

        return of((Class<? extends Enum<?>>) returnType);
    }

    public List<Map<String, Object>> getAvailableValues() {
        return availableValues;
    }
}
