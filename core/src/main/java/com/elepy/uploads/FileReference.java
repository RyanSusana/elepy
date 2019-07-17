package com.elepy.uploads;

import com.elepy.annotations.Delete;
import com.elepy.annotations.Identifier;
import com.elepy.annotations.RestModel;
import com.elepy.annotations.Update;
import com.elepy.routes.DisabledHandler;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestModel(name = "Files", slug = "/files")
@Update(handler = DisabledHandler.class)
@Delete(handler = UploadedFileDelete.class)
@Entity(name = "elepy_files")
@Table(name = "elepy_files")
public class FileReference {

    @Id
    @Identifier
    private String name;

    private String contentType;


    public FileReference() {
    }

    public FileReference(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    public static FileReference of(UploadedFile file) {
        return new FileReference(file.getName(), file.getContentType());
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


    public boolean contentTypeEquals(String contentTypeToCheck) {
        if (contentTypeToCheck.equals("*/*")) {
            return true;
        }

        Pattern p = Pattern.compile(contentTypeToCheck);
        Matcher m = p.matcher(this.contentType);
        return (m.find());
    }

}
