package com.elepy.revisions;

import com.elepy.annotations.*;
import com.elepy.json.RawJsonDeserializer;
import com.elepy.json.RawJsonSerializer;
import com.elepy.auth.Permissions;
import com.elepy.handlers.DisabledHandler;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

@Hidden
@Model(name = "Revision History", path = "/revisions")
@Update(handler = DisabledHandler.class, requiredPermissions = "disabled")
@Delete(handler = DisabledHandler.class, requiredPermissions = "disabled")
@Create(handler = DisabledHandler.class, requiredPermissions = "disabled")
@Find(requiredPermissions = Permissions.AUTHENTICATED, findManyHandler = RevisionFind.class, findOneHandler = RevisionFind.class)
public class Revision {
    @Id
    private String id;

    private String schemaPath;

    private String recordId;

    private String description;

    @Enumerated(EnumType.STRING)
    private RevisionType revisionType;
    private String userId;


    private Date timestamp;

    @JsonSerialize(using = RawJsonSerializer.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
    private String oldSnapshot;

    @JsonSerialize(using = RawJsonSerializer.class)
    @JsonDeserialize(using = RawJsonDeserializer.class)
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
