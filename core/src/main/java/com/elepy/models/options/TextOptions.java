package com.elepy.models.options;

import com.elepy.annotations.Text;
import com.elepy.models.TextType;

import java.lang.reflect.AnnotatedElement;

public class TextOptions implements Options {
    private int minimumLength;
    private int maximumLength;
    private TextType textType;

    public TextOptions(int minimumLength, int maximumLength, TextType textType) {
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.textType = textType;
    }

    public static TextOptions of(AnnotatedElement accessibleObject) {
        final Text annotation = accessibleObject.getAnnotation(Text.class);
        return new TextOptions(
                annotation == null ? 0 : annotation.minimumLength(),
                annotation == null ? Integer.MAX_VALUE : annotation.maximumLength(),
                annotation == null ? TextType.TEXTFIELD : annotation.value()
        );
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
