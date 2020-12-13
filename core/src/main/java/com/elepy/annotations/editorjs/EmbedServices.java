package com.elepy.annotations.editorjs;

import com.fasterxml.jackson.annotation.JsonProperty;

public @interface EmbedServices {
    @JsonProperty
    boolean coub() default true;

    @JsonProperty
    boolean youtube() default true;

    @JsonProperty("twitch-video")
    boolean twitchVideo() default true;

    @JsonProperty("twitch-channel")
    boolean twitchChannel() default true;

    @JsonProperty
    boolean imgur() default true;

    @JsonProperty
    boolean gfycat() default true;

    @JsonProperty
    boolean vimeo() default true;

    @JsonProperty
    boolean twitter() default true;

    @JsonProperty
    boolean instagram() default true;

}
