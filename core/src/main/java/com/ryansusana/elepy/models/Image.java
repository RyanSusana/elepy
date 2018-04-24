package com.ryansusana.elepy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.Date;

public class Image {
    @MongoId
    private final String id;

    @JsonProperty("upload_date")
    private final Date uploadDate;

    public Image(@JsonProperty("_id") String id, @JsonProperty("upload_date") Date uploadDate) {
        this.id = id;
        this.uploadDate = uploadDate;
    }


    public String getId() {
        return id;
    }

    public Image withHost(String host) {
        return new Image(host + "/images/" + id, uploadDate);
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public boolean isExternal() {
        return id.startsWith("http");
    }
}
