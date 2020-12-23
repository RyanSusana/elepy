package com.elepy.translations;

import com.elepy.annotations.Localized;

public class TranslationModel {
    @Localized
    private String toTranslate;

    public String getToTranslate() {
        return toTranslate;
    }

    public void setToTranslate(String toTranslate) {
        this.toTranslate = toTranslate;
    }
}
