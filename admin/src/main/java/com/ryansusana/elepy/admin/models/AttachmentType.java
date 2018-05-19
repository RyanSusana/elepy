package com.ryansusana.elepy.admin.models;

public enum AttachmentType {
    IMAGE("/images/"), STYLESHEET("/css/"), JAVASCRIPT("/js/"), FILE("/file/");


    private final String route;

    AttachmentType(String route) {
        this.route = route;
    }

    public static AttachmentType guessTypeFromMime(String mime) {
        if (mime.toLowerCase().contains("image")) {
            return IMAGE;
        } else if (mime.toLowerCase().contains("css")) {
            return STYLESHEET;
        } else if (mime.toLowerCase().contains("javascript")) {
            return JAVASCRIPT;
        } else {
            return FILE;
        }
    }

    public String getRoute() {
        return route;
    }
}
