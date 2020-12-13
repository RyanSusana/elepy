package com.elepy.annotations.editorjs;

import com.fasterxml.jackson.annotation.JsonProperty;

public @interface Embed {
    @JsonProperty
    boolean inlineToolbar() default false;

    @JsonProperty
    EmbedServices services() default @EmbedServices();
}
