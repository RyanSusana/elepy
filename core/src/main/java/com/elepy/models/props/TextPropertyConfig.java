package com.elepy.models.props;

import com.elepy.annotations.Text;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.models.TextType;

import java.lang.reflect.AccessibleObject;

public class TextPropertyConfig implements PropertyConfig {
    private final int minimumLength;
    private final int maximumLength;
    private final TextType textType;


    public TextPropertyConfig(int minimumLength, int maximumLength, TextType textType) {
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.textType = textType;
    }

    public static TextPropertyConfig of(AccessibleObject accessibleObject) {
        final Text annotation = accessibleObject.getAnnotation(Text.class);
        return new TextPropertyConfig(
                annotation == null ? 0 : annotation.minimumLength(),
                annotation == null ? Integer.MAX_VALUE : annotation.maximumLength(),
                annotation == null ? TextType.TEXTFIELD : annotation.value()
        );
    }

    public static TextPropertyConfig of(Property property) {
        return new TextPropertyConfig(property.getExtra("minimumLength"), property.getExtra("maximumLength"), property.getExtra("textType"));
    }

    @Override
    public void config(Property property) {
        property.setType(FieldType.TEXT);
        property.setExtra("textType", textType);
        property.setExtra("minimumLength", minimumLength);
        property.setExtra("maximumLength", maximumLength);
    }

    public int getMinimumLength() {
        return minimumLength;
    }

    public int getMaximumLength() {
        return maximumLength;
    }

    public TextType getTextType() {
        return textType;
    }
}
