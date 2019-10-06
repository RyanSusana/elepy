package com.elepy.admin.views;

public class CdnResourceLocation implements ResourceLocation {
    private final String cssLocation;
    private final String jsLocation;

    private CdnResourceLocation(String cssLocation, String jsLocation) {
        this.cssLocation = cssLocation;
        this.jsLocation = jsLocation;
    }

    public static CdnResourceLocation version(String version) {
        return new CdnResourceLocation(
                String.format("https://cdn.jsdelivr.net/npm/elepy-vue@%s/dist/ElepyVue.css", version),
                String.format("https://cdn.jsdelivr.net/npm/elepy-vue@%s/dist/ElepyVue.umd.min.js", version)
        );
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
