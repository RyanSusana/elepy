package com.elepy.annotations;

import com.elepy.annotations.editorjs.EditorJsDeserializer;
import com.elepy.annotations.editorjs.EditorJsProcessor;
import com.elepy.annotations.editorjs.EditorJsSerializer;
import com.elepy.annotations.editorjs.Embed;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE, ElementType.METHOD})
@ElepyAnnotationsInside
@JacksonAnnotationsInside
@Custom(processor = EditorJsProcessor.class)
@JsonDeserialize(using = EditorJsDeserializer.class)
@JsonSerialize(using = EditorJsSerializer.class)
public @interface EditorJs {
    Embed embed() default @Embed();
}
