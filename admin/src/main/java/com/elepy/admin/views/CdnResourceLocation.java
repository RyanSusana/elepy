package com.elepy.admin.views;

public class CdnResourceLocation implements ResourceLocation {
    private final String cssLocation;
    private final String jsLocation;

    public CdnResourceLocation(String cssLocation, String jsLocation) {
        this.cssLocation = cssLocation;
        this.jsLocation = jsLocation;
    }

    @Override
    public String getCssLocation() {
        return cssLocation;
    }

    @Override
    public String getJsLocation() {
        return jsLocation;
    }
}
