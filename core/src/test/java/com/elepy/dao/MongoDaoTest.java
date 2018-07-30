package com.elepy.dao;

import com.elepy.BaseFongoTest;
import com.elepy.concepts.Resource;
import com.elepy.dao.jongo.MongoDao;
import org.jongo.Jongo;
import org.junit.Before;

import static junit.framework.TestCase.assertEquals;

public class MongoDaoTest extends BaseFongoTest {

    private MongoDao<Resource> mongoDao;
    private Jongo jongo;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        mongoDao = new MongoDao<>(getDb(), "resources", Resource.class);
        jongo = new Jongo(getDb());
    }

    public void testCreate() {

        mongoDao.create(validObject());
        mongoDao.create(validObject());

        final long resources = jongo.getCollection("resources").count();

        assertEquals(2, resources);
    }

    public void testDelete() {
        final Resource resource = validObject();
        mongoDao.create(resource);
        mongoDao.delete(resource.getId());
        assertEquals(0, count());
    }


    public void testSearch() {

    }

    public void testGetById() {

    }

    private long count(){
        return jongo.getCollection("resources").count();
    }
}
