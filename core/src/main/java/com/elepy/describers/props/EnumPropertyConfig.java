package com.elepy.describers.props;

import com.elepy.describers.Property;
import com.elepy.models.FieldType;
import com.elepy.utils.MapperUtils;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Map;

public class EnumPropertyConfig implements PropertyConfig {

    private final List<Map<String, Object>> availableValues;

    public EnumPropertyConfig(List<Map<String, Object>> availableValues) {
        this.availableValues = availableValues;
    }

    public static EnumPropertyConfig of(AccessibleObject field) {
        final Class<?> returnType = ReflectionUtils.returnTypeOf(field);

        return of((Class<? extends Enum<?>>) returnType);
    }

    public static EnumPropertyConfig of(Class<? extends Enum<?>> returnType) {
        return new EnumPropertyConfig(MapperUtils.getEnumMapValues(returnType));
    }


    public static EnumPropertyConfig of(Property property) {
        return new EnumPropertyConfig(property.getExtra("availableValues"));
    }

    public List<Map<String, Object>> getAvailableValues() {
        return availableValues;
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.ENUM);
        property.setExtra("availableValues", availableValues);
    }
}
