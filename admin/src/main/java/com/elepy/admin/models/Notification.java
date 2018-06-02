package com.elepy.admin.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Notification {
    private final String id;
    private final String message;

    private final Date dateSent;

    private final Map<String, Object> metadata;

    private final List<User> seenBy;

    public Notification(String id, String message, Date dateSent, Map<String, Object> metadata, List<User> seenBy) {
        this.id = id;
        this.message = message;
        this.dateSent = dateSent;
        this.metadata = metadata;
        this.seenBy = seenBy;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public List<User> getSeenBy() {
        return seenBy;
    }
}
