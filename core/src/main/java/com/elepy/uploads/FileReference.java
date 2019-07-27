package com.elepy.uploads;

import com.elepy.annotations.*;
import com.elepy.routes.DisabledHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestModel(name = "Files", slug = "/files", shouldDisplayOnCMS = false)
@Update(handler = DisabledHandler.class)
@Delete(handler = FileReferenceDelete.class)
@Entity(name = "elepy_files")
@Table(name = "elepy_files")
public class FileReference {

    @Id
    @Identifier
    private String uid;

    @Column
    private String name;

    @Unique
    @Column(unique = true)
    private String uploadName;

    @Column
    private String contentType;

    // Better querying for cloud databases
    @Column
    private String mimeType;

    @Column
    private String mimeSubtype;

    @Column
    private long size;

    @Column
    private Date createdDate;


    public FileReference() {
    }

    public FileReference(String uid, String name, String uploadName, String contentType, long size, Date createdDate) {
        this.uid = uid;
        this.uploadName = uploadName;
        this.name = name;
        this.contentType = contentType;
        this.size = size;

        this.createdDate = createdDate;
        final String[] mime = contentType.split("/");

        this.mimeType = mime[0];
        this.mimeSubtype = mime[1].split(";")[0];
    }

    public static FileReference newFileReference(FileUpload file) {
        return new FileReference(UUID.randomUUID().toString(), file.getName(), file.getName(), file.getContentType(), file.getSize(), Calendar.getInstance().getTime());
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeSubtype() {
        return mimeSubtype;
    }

    public void setMimeSubtype(String mimeSubtype) {
        this.mimeSubtype = mimeSubtype;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean contentTypeEquals(String contentTypeToCheck) {
        if (contentTypeToCheck.equals("*/*")) {
            return true;
        }

        Pattern p = Pattern.compile(contentTypeToCheck);
        Matcher m = p.matcher(this.contentType);
        return (m.find());
    }

}
