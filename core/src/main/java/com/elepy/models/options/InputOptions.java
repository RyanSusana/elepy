package com.elepy.models.options;

import com.elepy.annotations.Input;
import com.elepy.utils.Annotations;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class InputOptions implements Options {
    private final String inputType;

    public InputOptions(String inputType) {
        this.inputType = inputType;
    }

    public static InputOptions of(AnnotatedElement accessibleObject) {
        final var type = Optional.ofNullable(Annotations.get(accessibleObject, Input.class))
                .map(Input::type)
                .orElse("text");
        return new InputOptions(
                type
        );
    }

    public String getInputType() {
        return inputType;
    }
}
