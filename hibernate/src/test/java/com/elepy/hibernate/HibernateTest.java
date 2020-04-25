package com.elepy.hibernate;

import com.elepy.dao.Crud;
import com.elepy.dao.Filters;
import com.elepy.dao.Queries;
import com.elepy.di.DefaultElepyContext;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.utils.ModelUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static com.elepy.dao.Filters.eq;
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
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.registerDependency(SessionFactory.class, sessionFactory);
        defaultElepyContext.registerDependency(new ObjectMapper());


        resourceCrud = defaultElepyContext.initialize(HibernateCrudFactory.class).crudFor(ModelUtils.createDeepSchema(Resource.class));


    }

    @Test
    public void testCreate() {

        resourceCrud.create(validObject());
        resourceCrud.create(validObject());


        assertThat(count()).isEqualTo(2);
    }

    @Test
    public void testFilterEndToEnd() throws IOException {
        Resource resource = validObject();
        resource.setUnique("filterUnique");
        resource.setId(4);
        resourceCrud.create(resource);
        resourceCrud.create(validObject());

        long tenSecondsFromNow = Calendar.getInstance().getTimeInMillis() + 10000;
        long tenSecondsBefore = Calendar.getInstance().getTimeInMillis() - 10000;

        Map<String, String> map = new HashMap<>();

        map.put("id_equals", "4");
        map.put("date_gt", "" + tenSecondsBefore);
        map.put("date_lte", "" + tenSecondsFromNow);
        map.put("unique_contains", "filt");

        Request request = mockedContextWithQueryMap(map).request();

        List<Resource> resourceResult = resourceCrud.find(
                new com.elepy.dao.Query(Filters.and(request.filtersForModel(Resource.class)))
        );

        assertThat(resourceResult.size()).isEqualTo(1);

        assertThat(resourceResult.get(0).getUnique()).isEqualTo("filterUnique");
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

    private HttpContext mockedContextWithQueryMap(Map<String, String> map) {
        HttpContext mockedContext = mockedContext();
        when(mockedContext.request().queryParams()).thenReturn(map.keySet());

        when(mockedContext.request().queryParams(anyString())).thenAnswer(invocationOnMock -> map.get(invocationOnMock.getArgument(0)));

        when(mockedContext.request().filtersForModel(any())).thenCallRealMethod();
        return mockedContext;
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
