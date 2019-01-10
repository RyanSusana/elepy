package com.elepy.di;

import com.elepy.Base;
import com.elepy.Elepy;
import com.elepy.concepts.Resource;
import com.elepy.dao.Crud;
import com.elepy.dao.jongo.MongoDao;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.FongoDB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContextTest extends Base {

    @Test
    void testCrudFor() {
        Elepy elepy = new Elepy();
        Fongo fongo = new Fongo("test");
        final FongoDB db = fongo.getDB("test");
        MongoDao<Resource> mongoDao = new MongoDao<>(db, "resources", Resource.class);


        elepy.attachSingleton(DB.class, db).addModel(Resource.class).onPort(1111).start();

        Crud<Resource> crudFor = elepy.getContext().getCrudFor(Resource.class);

        assertNotNull(crudFor);
        assertTrue(crudFor instanceof MongoDao);

        MongoDao<Resource> newMongoDao = (MongoDao<Resource>) crudFor;
        assertEquals(mongoDao.getClassType(), newMongoDao.getClassType());
    }
}
