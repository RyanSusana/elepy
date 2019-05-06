package com.elepy.describers.props;

import com.elepy.annotations.TrueFalse;
import com.elepy.describers.Property;
import com.elepy.models.FieldType;

import java.lang.reflect.AccessibleObject;

public class BooleanPropertyConfig implements PropertyConfig {
    private final String trueValue;
    private final String falseValue;

    public BooleanPropertyConfig(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    public static BooleanPropertyConfig of(AccessibleObject field) {
        final TrueFalse annotation = field.getAnnotation(TrueFalse.class);
        return new BooleanPropertyConfig(
                annotation == null ? "true" : annotation.trueValue(),
                annotation == null ? "false" : annotation.falseValue()
        );
    }

    public static BooleanPropertyConfig of(Property property) {
        return new BooleanPropertyConfig(
                property.getExtra("trueValue"),
                property.getExtra("falseValue")
        );
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.BOOLEAN);
        property.setExtra("trueValue", trueValue);
        property.setExtra("falseValue", falseValue);
    }
}
