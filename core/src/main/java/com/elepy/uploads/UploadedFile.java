package com.elepy.uploads;

import com.elepy.annotations.*;
import com.elepy.routes.DisabledHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.InputStream;

@RestModel(name = "Files", slug = "/files")
@Update(handler = DisabledHandler.class)
@Delete(handler = UploadedFileDelete.class)
public class UploadedFile {

    private String id;

    private String contentType;

    @Unique
    private String name;

    @JsonIgnore
    @Hidden
    private InputStream content;

    public UploadedFile() {

    }

    public UploadedFile(String contentType, InputStream content, String name) {
        this(contentType, content, name, name);
    }

    public UploadedFile(String contentType, InputStream content, String name, String id) {
        this.contentType = contentType;
        this.content = content;
        this.name = name;
        this.id = id;
    }

    public static UploadedFile of(String contentType, InputStream content, String name) {
        return new UploadedFile(contentType, content, name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
