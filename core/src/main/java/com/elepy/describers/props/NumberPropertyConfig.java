package com.elepy.describers.props;

import com.elepy.annotations.Number;
import com.elepy.describers.Property;
import com.elepy.models.FieldType;
import com.elepy.models.NumberType;
import com.elepy.utils.ReflectionUtils;

import java.lang.reflect.AccessibleObject;

public class NumberPropertyConfig implements PropertyConfig {
    private final float minimum;
    private final float maximum;
    private final NumberType numberType;

    public NumberPropertyConfig(float minimum, float maximum, NumberType numberType) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.numberType = numberType;
    }

    public static NumberPropertyConfig of(AccessibleObject field) {
        final Number annotation = field.getAnnotation(Number.class);
        return new NumberPropertyConfig(
                annotation == null ? Integer.MIN_VALUE : annotation.minimum(),
                annotation == null ? Integer.MAX_VALUE : annotation.maximum(),
                NumberType.guessType(ReflectionUtils.returnType(field))

        );
    }

    public static NumberPropertyConfig of(Property property) {
        return new NumberPropertyConfig(
                property.getExtra("minimum"),
                property.getExtra("maximum"),
                property.getExtra("numberType")
        );
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.NUMBER);
        property.setExtra("minimum", minimum);
        property.setExtra("maximum", maximum);
        property.setExtra("numberType", numberType);
    }

    public float getMinimum() {
        return minimum;
    }

    public float getMaximum() {
        return maximum;
    }
}
