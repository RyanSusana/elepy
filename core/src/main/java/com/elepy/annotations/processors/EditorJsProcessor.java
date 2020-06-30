package com.elepy.annotations.processors;

import com.elepy.models.options.CustomOptions;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.function.Function;

public class EditorJsProcessor implements Function<AnnotatedElement, CustomOptions> {

    private static final String EDITOR_JS_UMD = "/elepy/js/EditorJsField.umd.min.js";

    @Override
    public CustomOptions apply(AnnotatedElement element) {
        return new CustomOptions(EDITOR_JS_UMD, Map.of());
    }
}
