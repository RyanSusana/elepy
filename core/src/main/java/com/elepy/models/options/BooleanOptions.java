package com.elepy.models.options;

import com.elepy.annotations.TrueFalse;

import java.lang.reflect.AccessibleObject;

public class BooleanOptions implements Options {

    private String trueValue;
    private String falseValue;

    private BooleanOptions(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }


    public static BooleanOptions of(AccessibleObject field) {
        final TrueFalse annotation = field.getAnnotation(TrueFalse.class);
        return new BooleanOptions(
                annotation == null ? "true" : annotation.trueValue(),
                annotation == null ? "false" : annotation.falseValue()
        );
    }

    public String getFalseValue() {
        return falseValue;
    }

    public String getTrueValue() {
        return trueValue;
    }
}
