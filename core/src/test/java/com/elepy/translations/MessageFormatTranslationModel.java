package com.elepy.translations;

import com.elepy.annotations.Localized;
import com.elepy.exceptions.Translated;

public class MessageFormatTranslationModel {
    @Localized
    private Translated toTranslate;

    public Translated getToTranslate() {
        return toTranslate;
    }

    public void setToTranslate(Translated toTranslate) {
        this.toTranslate = toTranslate;
    }
}
