package com.elepy.test.e2e.user;

import com.elepy.Configuration;
import com.elepy.mongo.MongoConfiguration;
import com.github.fakemongo.Fongo;
import com.mongodb.FongoDB;

public class MongoUserEndToEndTest extends ElepyUserEndToEndTest {

    @Override
    public Configuration configuration() {
        Fongo fongo = new Fongo("test");
        final FongoDB db = fongo.getDB("test");

        return MongoConfiguration.of(db);
    }
}
