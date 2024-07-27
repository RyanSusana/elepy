package com.elepy.models.options;

import com.elepy.models.FieldType;

import java.lang.reflect.AnnotatedElement;

public class OptionFactory {


    public Options getOptions(AnnotatedElement field, FieldType fieldType) {
        return switch (fieldType) {
            case INPUT -> InputOptions.of(field);
            case DATE -> DateOptions.of(field);
            case NUMBER -> NumberOptions.of(field);
            case ENUM -> EnumOptions.of(field);
            case OBJECT -> ObjectOptions.of(field);
            case BOOLEAN -> BooleanOptions.of(field);
            case ARRAY -> ArrayOptions.of(field);
            case FILE_REFERENCE -> FileReferenceOptions.of(field);
            case REFERENCE -> ReferenceOptions.of(field);
            case CUSTOM -> CustomOptions.of(field);
            // TODO: refactor away
            case TEXTAREA, MARKDOWN, HTML, DYNAMIC -> null;
        };
    }
}
