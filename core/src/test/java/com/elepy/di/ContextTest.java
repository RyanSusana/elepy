package com.elepy.di;

import com.elepy.Base;
import com.elepy.Elepy;
import com.elepy.concepts.Resource;
import com.elepy.dao.Crud;
import com.elepy.dao.jongo.MongoDao;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.FongoDB;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContextTest extends Base {


    @BeforeAll
    public static void beforeAll() throws Exception {


    }

    @Test
    void testCrudFor() {
        Elepy elepy = new Elepy();
        Fongo fongo = new Fongo("test");
        final FongoDB db = fongo.getDB("test");
        MongoDao<Resource> mongoDao = new MongoDao<>(db, "resources", Resource.class);


        elepy.attachSingleton(DB.class, db).includeModel(Resource.class).onPort(1111).start();

        Crud<Resource> crudFor = elepy.getContext().getCrudFor(Resource.class);

        assertNotNull(crudFor);
        assertTrue(crudFor instanceof MongoDao);
    }
}
