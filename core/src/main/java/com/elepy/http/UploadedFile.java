package com.elepy.http;

import java.io.InputStream;

public class UploadedFile {


    private final String contentType;

    private final String name;

    private final String extension;


    private final InputStream content;


    public UploadedFile(String contentType, InputStream content, String name, String extension) {
        this.contentType = contentType;
        this.content = content;
        this.name = name;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public static UploadedFile of(String contentType, InputStream content, String name, String extension) {
        return new UploadedFile(contentType, content, name, extension);
    }

    public InputStream getContent() {
        return content;
    }


}
