package com.elepy.hibernate;

import com.elepy.crud.Crud;
import com.elepy.query.Filters;
import com.elepy.query.Queries;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.schemas.SchemaFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static com.elepy.query.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HibernateTest {

    private static int resourceCounter = -100;
    private Crud<Resource> resourceCrud;
    private SessionFactory sessionFactory;

    @BeforeEach
    public void setUp() throws Exception {

        Configuration hibernateConfiguration = new Configuration().configure();

        hibernateConfiguration.addAnnotatedClass(Resource.class);

        sessionFactory = hibernateConfiguration.buildSessionFactory();
//        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
//        defaultElepyContext.registerDependency(SessionFactory.class, sessionFactory);
//        defaultElepyContext.registerDependency(new ObjectMapper());
//
//
//        resourceCrud = defaultElepyContext.initialize(HibernateCrudFactory.class).crudFor(new SchemaFactory().createDeepSchema(Resource.class));


    }

    @Test
    public void testCreate() {

        resourceCrud.create(validObject());
        resourceCrud.create(validObject());


        assertThat(count()).isEqualTo(2);
    }


    @Test
    public void testDelete() {
        final Resource resource = validObject();
        resourceCrud.create(resource);
        resourceCrud.deleteById(resource.getId());
        assertThat(count()).isEqualTo(0);
    }


    @Test
    public void testSearch() {

        final Resource resource = validObject();
        resourceCrud.create(resource);


        final List<Resource> searchable = resourceCrud.find(Queries.create(Filters.search("searcha"))
        );
        assertThat(searchable.size()).isEqualTo(1);

    }

    @Test
    public void testSearchInField() throws NoSuchFieldException {

        final Resource resource = validObject();
        resourceCrud.create(resource);


        Field searchableField = Resource.class.getDeclaredField("searchableField");
        searchableField.setAccessible(true);
        final List<Resource> searchable = resourceCrud.find(eq("searchableField", "searchable"));
        assertThat(searchable.size()).isEqualTo(1);

    }

    @Test
    public void testMultiCreate() {
        final Resource resource = validObject();
        final Resource resource2 = validObject();

        resourceCrud.create(Arrays.asList(resource, resource2));

        assertThat(count()).isEqualTo(2);


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
        resource.setUnique("unique");
        resource.setDate(Calendar.getInstance().getTime());

        return resource;
    }

    private HttpContext mockedContext() {
        HttpContext context = mock(HttpContext.class);
        Request request = mock(Request.class);
        Response response = mock(Response.class);


        when(context.response()).thenReturn(response);
        when(context.request()).thenReturn(request);


        return context;
    }
}
