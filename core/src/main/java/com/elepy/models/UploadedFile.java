package com.elepy.models;

import java.io.InputStream;

public class UploadedFile {
    private final String contentType;

    private final InputStream content;

    private final String name;

    private final String extension;

    private UploadedFile(String contentType, InputStream content, String name, String extension) {
        this.contentType = contentType;
        this.content = content;
        this.name = name;
        this.extension = extension;
    }

    public static UploadedFile of(String contentType, InputStream content, String name, String extension) {
        return new UploadedFile(contentType, content, name, extension);
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }
}
