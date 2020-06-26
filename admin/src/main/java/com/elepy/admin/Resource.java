package com.elepy.admin;

public class Resource {
    private final String path;
    private final String contentType;
    private final String location;


    public Resource(String path, String contentType, String raw) {
        this.path = path;
        this.contentType = contentType;
        this.location = raw;
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }

    public String getLocation() {
        return location;
    }
}
