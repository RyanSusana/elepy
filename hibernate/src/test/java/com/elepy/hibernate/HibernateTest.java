package com.elepy.hibernate;

import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.QuerySetup;
import com.elepy.di.DefaultElepyContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HibernateTest {

    private Crud<Resource> resourceCrud;

    private SessionFactory sessionFactory;

    @BeforeEach
    public void setUp() throws Exception {

        Configuration hibernateConfiguration = new Configuration().configure();


        sessionFactory = hibernateConfiguration.buildSessionFactory();
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.registerDependency(SessionFactory.class, sessionFactory);
        defaultElepyContext.registerDependency(new ObjectMapper());


        resourceCrud = defaultElepyContext.initializeElepyObject(HibernateProvider.class).crudFor(Resource.class);

    }

    @Test
    public void testCreate() {

        resourceCrud.create(validObject());
        resourceCrud.create(validObject());


        assertEquals(2, count());
    }

    @Test
    public void testDelete() {
        final Resource resource = validObject();
        resourceCrud.create(resource);
        resourceCrud.delete(resource.getId());
        assertEquals(0, count());
    }


    @Test
    public void testSearch() {

        final Resource resource = validObject();
        resourceCrud.create(resource);


        final Page<Resource> searchable = resourceCrud.search(new QuerySetup("searchab", null, null, 1L, 1));
        assertEquals(1, searchable.getValues().size());

    }

    @Test
    public void testCountSearch() {

        final Resource resource = validObject();
        resourceCrud.create(resource);


        final long searchable = resourceCrud.count("searcha");
        assertEquals(1, searchable);

    }

    @Test
    public void testSearchInField() throws NoSuchFieldException {

        final Resource resource = validObject();
        resourceCrud.create(resource);


        Field searchableField = Resource.class.getDeclaredField("searchableField");
        searchableField.setAccessible(true);
        final List<Resource> searchable = resourceCrud.searchInField(searchableField, "searchable");
        assertEquals(1, searchable.size());

    }

    @Test
    public void testMultiCreate() {
        final Resource resource = validObject();
        final Resource resource2 = validObject();

        resourceCrud.create(Arrays.asList(resource, resource2));

        assertEquals(2, count());


    }

    private long count() {
        try (Session session = sessionFactory.openSession()) {
            final Query<Long> query = session.createQuery("select count(*) from " + Resource.class.getName(), Long.class);
            return query.getSingleResult();
        }
    }

    public Resource validObject() {
        Resource resource = new Resource();

        resource.setTextField("textfield");
        resource.setSearchableField("searchable");
        resource.setUniqueField("unique");

        return resource;
    }
}
