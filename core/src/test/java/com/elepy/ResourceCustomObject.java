package com.elepy;

import com.elepy.annotations.Featured;

public class ResourceCustomObject {

    @Featured
    private String featured;

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }
}
