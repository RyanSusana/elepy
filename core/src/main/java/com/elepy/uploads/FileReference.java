package com.elepy.uploads;

import com.elepy.annotations.*;
import com.elepy.auth.Permissions;
import com.elepy.dao.SortOption;
import com.elepy.handlers.DisabledHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Model(name = "Files", path = "/files", defaultSortDirection = SortOption.DESCENDING, defaultSortField = "createdDate")
@Update(handler = DisabledHandler.class)
@Delete(handler = FileReferenceDelete.class, requiredPermissions = Permissions.CAN_ADMINISTRATE_FILES)
@Entity(name = "elepy_files")
@Table(name = "elepy_files")
@View(View.Defaults.FILE)
public class FileReference {

    @Id
    @Identifier
    @com.elepy.annotations.FileReference
    @Uneditable
    @PrettyName("File")
    @Searchable
    private String uploadName;

    @Column
    @PrettyName("Name")
    @Searchable
    private String name;

    @Column
    @PrettyName("Content Type")
    @Uneditable
    private String contentType;

    // Better querying for cloud databases
    @Column
    @Uneditable
    @Importance(-100)
    @PrettyName("Main Type")
    private String mimeMainType;

    @Column
    @Uneditable
    @Importance(-100)
    @PrettyName("Subtype")
    private String mimeSubType;

    @Column
    @Uneditable
    @PrettyName("Size (Bytes)")
    private long size;

    @Column
    @Uneditable
    @PrettyName("Created")
    private Date createdDate;


    public FileReference() {
    }

    public FileReference(String name, String uploadName, String contentType, long size, Date createdDate) {

        this.uploadName = uploadName;
        this.name = name;
        this.contentType = contentType;
        this.size = size;

        this.createdDate = createdDate;
        final String[] mime = contentType.split("/");

        this.mimeMainType = mime[0];
        this.mimeSubType = mime[1].split(";")[0];
    }

    public static FileReference newFileReference(FileUpload file) {
        return new FileReference(file.getName(), file.getName(), file.getContentType(), file.getSize(), Calendar.getInstance().getTime());
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public String getMimeMainType() {
        return mimeMainType;
    }

    public void setMimeMainType(String mimeMainType) {
        this.mimeMainType = mimeMainType;
    }

    public String getMimeSubType() {
        return mimeSubType;
    }

    public void setMimeSubType(String mimeSubType) {
        this.mimeSubType = mimeSubType;
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
