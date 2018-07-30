package com.elepy.dao;

import com.elepy.concepts.Resource;
import com.elepy.dao.jongo.MongoDao;
import com.mongodb.DB;

public class ResourceDao extends MongoDao<Resource> {
    public ResourceDao(DB db, String collectionName, Class<Resource> classType) {
        super(db, collectionName, classType);
    }
}
