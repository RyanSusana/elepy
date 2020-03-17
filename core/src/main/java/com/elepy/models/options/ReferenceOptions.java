package com.elepy.models.options;

import com.elepy.annotations.Reference;
import com.elepy.models.Schema;
import com.elepy.utils.ModelUtils;

import java.lang.reflect.AnnotatedElement;

public class ReferenceOptions implements Options {

    private final Schema<?> referenceSchema;


    public ReferenceOptions(Schema<?> to) {
        this.referenceSchema = to;
    }

    public static ReferenceOptions of(AnnotatedElement element) {
        final var reference = element.getAnnotation(Reference.class);
        return new ReferenceOptions(ModelUtils.createBasicSchema(reference.to()));
    }

    public Schema<?> getReferenceSchema() {
        return referenceSchema;
    }
}
