package com.elepy.plugins.gallery;

import com.elepy.annotations.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;

public class Image {

    @Identifier
    private String id;


    private String original;
    private String caption;


    @JsonProperty("upload_date")
    private Date uploadDate;

    public Image() {
    }

    public Image(String fileName) {
        this(fileName, "/images/" + fileName + "-original", "/images/" + fileName + "-caption", Calendar.getInstance().getTime());
    }

    public Image(String id, String original, String caption, Date uploadDate) {
        this.id = id;
        this.original = original;
        this.caption = caption;
        this.uploadDate = uploadDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

}
