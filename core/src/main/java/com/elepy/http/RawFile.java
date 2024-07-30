package com.elepy.http;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawFile {

    private String contentType;

    private String name;

    private InputStream content;

    private long size;

    private RawFile(String contentType, InputStream content, String name, long size) {
        this.contentType = contentType;
        this.content = content;
        this.name = name;
        this.size = size;
    }

    public static RawFile of(String name, String contentType, InputStream content, long size) {
        return new RawFile(contentType, content, name, size);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public boolean contentTypeMatches(String contentTypeToCheck) {
        if (contentTypeToCheck.equals("*/*")) {
            return true;
        }

        Pattern p = Pattern.compile(contentTypeToCheck);
        Matcher m = p.matcher(this.contentType.split(";")[0]);
        return (m.find());
    }

}
