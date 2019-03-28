package com.elepy.hibernate;

import com.elepy.Elepy;
import com.elepy.dao.Crud;
import com.elepy.dao.Page;
import com.elepy.dao.SearchQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HibernateTest {

    private static int resourceCounter = -100;
    private Crud<Resource> resourceCrud;
    private SessionFactory sessionFactory;
    Elepy elepy;

    @BeforeEach
    public void setUp() throws Exception {

        elepy = new Elepy();
        Configuration hibernateConfiguration = new Configuration().configure();


        sessionFactory = hibernateConfiguration.buildSessionFactory();

        elepy.registerDependency(SessionFactory.class, sessionFactory);

        elepy.addModel(Resource.class);

        elepy.onPort(8358);

        elepy.withDefaultCrudProvider(HibernateProvider.class);
        elepy.start();


        resourceCrud = elepy.getCrudFor(Resource.class);

    }

    @Test
    public void testCreate() {

        resourceCrud.create(validObject());
        resourceCrud.create(validObject());


        assertEquals(2, count());
    }

    @Test
    public void testFilterEndToEnd() throws IOException, UnirestException {
        Resource resource = validObject();
        resource.setUniqueField("filterUnique");
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        long tenSecondsFromNow = Calendar.getInstance().getTimeInMillis() + 10000;
        long tenSecondsBefore = Calendar.getInstance().getTimeInMillis() - 10000;
        final HttpResponse<String> getRequest = Unirest.get(String.format("http://localhost:8358/resources?id_equals=4&uniqueField_contains=filter&date_lte=%d&date_gt=%d", tenSecondsFromNow, tenSecondsBefore)).asString();


        Page<Resource> resourcePage = elepy.getObjectMapper().readValue(getRequest.getBody(), new TypeReference<Page<Resource>>() {
        });

        assertEquals(1, resourcePage.getValues().size());

        assertEquals(200, getRequest.getStatus());
        assertEquals("filterUnique", resourcePage.getValues().get(0).getUniqueField());
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


        final Page<Resource> searchable = resourceCrud.search(new SearchQuery("searchab", null, null, 1L, 1));
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
        final List<Resource> searchable = resourceCrud.searchInField("searchableField", "searchable");
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

        resource.setId(resourceCounter++);
        resource.setTextField("textfield");
        resource.setSearchableField("searchable");
        resource.setUniqueField("unique");
        resource.setDate(Calendar.getInstance().getTime());

        return resource;
    }
}
