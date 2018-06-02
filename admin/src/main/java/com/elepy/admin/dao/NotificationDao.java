package com.elepy.admin.dao;

import com.elepy.admin.models.Notification;
import com.elepy.dao.MongoDao;
import com.mongodb.DB;

import java.util.List;

public class NotificationDao extends MongoDao<Notification> {

    private final UserDao userDao;

    public NotificationDao(DB db) {
        super(db, "notifications", Notification.class);
        this.userDao = new UserDao(db);
    }

    public List<Notification> getNotSeenBy() {
        collection().find().as(Notification.class);
        return null;
    }
}
