package com.elepy.translations;

import com.elepy.annotations.Localized;
import com.elepy.exceptions.TranslatedMessage;

public class MessageFormatTranslationModel {
    @Localized
    private TranslatedMessage toTranslate;

    public TranslatedMessage getToTranslate() {
        return toTranslate;
    }

    public void setToTranslate(TranslatedMessage toTranslate) {
        this.toTranslate = toTranslate;
    }
}
