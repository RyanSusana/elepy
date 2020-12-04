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

    private String description;
    private RevisionType revisionType;
    private String userId;


    private Date timestamp;

    private String oldSnapshot;
    private String newSnapshot;

    public String getNewSnapshot() {
        return newSnapshot;
    }

    public void setNewSnapshot(String newSnapshot) {
        this.newSnapshot = newSnapshot;
    }


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getOldSnapshot() {
        return oldSnapshot;
    }

    public void setOldSnapshot(String oldSnapshot) {
        this.oldSnapshot = oldSnapshot;
    }

    public RevisionType getRevisionType() {
        return revisionType;
    }

    public void setRevisionType(RevisionType revisionType) {
        this.revisionType = revisionType;
    }
}
