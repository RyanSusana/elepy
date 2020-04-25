package com.elepy.revisions;

import com.elepy.annotations.Hidden;
import com.elepy.annotations.Model;

import javax.persistence.Id;
import java.util.Date;

@Hidden
@Model(name = "Revision History", path = "/revisions")
public class Revision {
    // Global identifier
    @Id
    private String id;

    //
    private String schemaPath;

    // Id of the record
    private String recordId;
    private Integer revisionNumber;

    private String revisionName;
    private RevisionType revisionType;
    private String userId;


    private Date timestamp;
    private String recordSnapshot;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchemaPath() {
        return schemaPath;
    }

    public void setSchemaPath(String modelPath) {
        this.schemaPath = modelPath;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getRevisionName() {
        return revisionName;
    }

    public void setRevisionName(String revisionName) {
        this.revisionName = revisionName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecordSnapshot() {
        return recordSnapshot;
    }

    public void setRecordSnapshot(String recordSnapshot) {
        this.recordSnapshot = recordSnapshot;
    }

    public RevisionType getRevisionType() {
        return revisionType;
    }

    public void setRevisionType(RevisionType revisionType) {
        this.revisionType = revisionType;
    }
}
