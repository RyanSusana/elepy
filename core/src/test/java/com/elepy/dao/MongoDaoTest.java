package com.elepy.dao;

import com.elepy.BaseFongoTest;
import com.elepy.concepts.Resource;
import com.elepy.dao.jongo.MongoDao;
import org.jongo.Jongo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MongoDaoTest extends BaseFongoTest {

    private MongoDao<Resource> mongoDao;
    private Jongo jongo;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        mongoDao = new MongoDao<>(getDb(), "resources", Resource.class);
        jongo = new Jongo(getDb());
    }

    @Test
    public void testCreate() {

        mongoDao.create(validObject());
        mongoDao.create(validObject());

        final long resources = jongo.getCollection("resources").count();

        assertEquals(2, resources);
    }

    @Test
    public void testDelete() {
        final Resource resource = validObject();
        mongoDao.create(resource);
        mongoDao.delete(resource.getId());
        assertEquals(0, count());
    }


    @Test
    public void testSearch() {

        final Resource resource = validObject();
        mongoDao.create(resource);


        final Page<Resource> searchable = mongoDao.search(new QuerySetup("searchable", null, null, 1L, 1));
        assertEquals(1, searchable.getValues().size());

    }

    @Test
    public void testCountSearch() {

        final Resource resource = validObject();
        mongoDao.create(resource);


        final long searchable = mongoDao.count("searchable");
        assertEquals(1, searchable);

    }
    @Test
    public void testMultiCreate(){
        final Resource resource = validObject();
        final Resource resource2 = validObject();

        resource2.setUnique("Unique2");

        mongoDao.create(Arrays.asList(resource,resource2));

        assertEquals(2, count());


    }

    public void testGetById() {

    }

    private long count() {
        return jongo.getCollection("resources").count();
    }
}
