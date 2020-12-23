package com.elepy.i18n;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ReturnedMessage {

    @JsonProperty
    String getMessage();

    @JsonProperty
    int getStatus();
} 
