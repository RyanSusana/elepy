package com.elepy.uploads;

import com.elepy.annotations.Delete;
import com.elepy.annotations.Identifier;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Update;
import com.elepy.routes.DisabledHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestModel(name = "Files", slug = "/files")
@Update(handler = DisabledHandler.class)
@Delete(handler = FileReferenceDelete.class)
@Entity(name = "elepy_files")
@Table(name = "elepy_files")
public class FileReference {

    @Id
    @Identifier
    private String name;

    @Column
    private String contentType;

    //Like this to query easier for cloud databases
    @Column
    private String mimeType;
    @Column
    private String mimeSubtype;

    @Column
    private long size;


    public FileReference() {
    }

    public FileReference(String name, String contentType, long size) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;

        final String[] mime = contentType.split("/");

        this.mimeType = mime[0];
        this.mimeSubtype = mime[1].split(";")[0];
    }

    public static FileReference of(FileUpload file) {
        return new FileReference(file.getName(), file.getContentType(), file.getSize());
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
