package com.elepy.annotations.editorjs;

import com.elepy.annotations.EditorJs;
import com.elepy.models.options.CustomOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.function.Function;

public class EditorJsProcessor implements Function<AnnotatedElement, CustomOptions> {

    private static final String EDITOR_JS_UMD = "/elepy/js/EditorJsField.umd.min.js";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CustomOptions apply(AnnotatedElement element) {

        final var editorJs = element.getAnnotation(EditorJs.class);
        final Embed embed = editorJs.embed();
        return new CustomOptions(EDITOR_JS_UMD, Map.of("embed", objectMapper.convertValue(embed, Map.class)));
    }
}
