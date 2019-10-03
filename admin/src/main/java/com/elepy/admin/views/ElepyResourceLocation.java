package com.elepy.admin.views;

public class ElepyResourceLocation {
    private final String cssLocation;
    private final String jsLocation;

    public ElepyResourceLocation(String cssLocation, String jsLocation) {
        this.cssLocation = cssLocation;
        this.jsLocation = jsLocation;
    }

    public String getCssLocation() {
        return cssLocation;
    }

    public String getJsLocation() {
        return jsLocation;
    }
}
