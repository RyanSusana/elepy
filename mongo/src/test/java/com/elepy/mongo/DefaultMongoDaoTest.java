package com.elepy.mongo;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.PageSettings;
import com.elepy.dao.Query;
import com.elepy.di.DefaultElepyContext;
import com.elepy.utils.ModelUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Jongo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DefaultMongoDaoTest extends BaseFongo {

    private Crud<Resource> defaultMongoDao;
    private Jongo jongo;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.registerDependency(DB.class, getDb());
        defaultElepyContext.registerDependency(new ObjectMapper());

        defaultMongoDao = defaultElepyContext.initialize(MongoCrudFactory.class).crudFor(ModelUtils.createModelFromClass(Resource.class));

        jongo = new Jongo(getDb());
    }

    @Test
    public void testCreate() {
        final Resource resource1 = validObject();
        resource1.setTextField("create");
        final Resource resource2 = validObject();
        defaultMongoDao.create(resource1);
        defaultMongoDao.create(resource2);

        final long resources = jongo.getCollection("resources").count();

        assertEquals(2, resources);

        assertThat(jongo.getCollection("resources").findOne("{_id: #}" + resource1.getId(), resource1.getId()).as(Resource.class).getTextField())
                .isEqualTo("create");
        assertThat(jongo.getCollection("resources").findOne("{id: #}" + resource1.getId(), resource1.getId()).as(Resource.class).getTextField())
                .isEqualTo("create");
    }

    @Test
    public void testDelete() {
        final Resource resource = validObject();
        defaultMongoDao.create(resource);
        defaultMongoDao.deleteById(resource.getId());
        assertEquals(0, count());
    }


    @Test
    public void testSearch() {

        final Resource resource = validObject();
        defaultMongoDao.create(resource);


        final Page<Resource> searchable = defaultMongoDao.search(new Query("sear", new ArrayList<>()), new PageSettings(1, Integer.MAX_VALUE, new ArrayList<>()));
        assertEquals(1, searchable.getValues().size());

    }

    @Test
    public void testCountSearch() {

        final Resource resource = validObject();
        defaultMongoDao.create(resource);


        final long searchable = defaultMongoDao.count("searchab");
        assertEquals(1, searchable);

    }

    @Test
    public void testMultiCreate() {
        final Resource resource = validObject();
        final Resource resource2 = validObject();

        resource2.setUnique("Unique2");

        defaultMongoDao.create(Arrays.asList(resource, resource2));

        assertEquals(2, count());


    }

    @Test
    void testUpdateWithPrototype() {
        final Resource resource = validObject();
        final Resource resource2 = validObject();

        resource2.setUnique("Unique2");

        defaultMongoDao.create(Arrays.asList(resource, resource2));

        final Map<String, Object> prototype = new HashMap<>();

        prototype.put("textField", "NEW_VALUE");
        prototype.put("unique", "NEW_UNIQUE_VAL");
        defaultMongoDao.updateWithPrototype(prototype, resource.getId(), resource2.getId());

        final List<Resource> updatedTextFieldResources = defaultMongoDao.searchInField("textField", "NEW_VALUE");
        final List<Resource> updatedUniqueResources = defaultMongoDao.searchInField("unique", "NEW_UNIQUE_VAL");

        assertEquals(2, updatedTextFieldResources.size());
        assertEquals(0, updatedUniqueResources.size());

    }

    public void testGetById() {

    }

    private long count() {
        return jongo.getCollection("resources").count();
    }
}
