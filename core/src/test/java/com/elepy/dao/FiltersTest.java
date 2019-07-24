package com.elepy.dao;

import com.elepy.Resource;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FiltersTest {

    @Test
    void testProperTestSetup() {
        Map<String, String> map = new HashMap<>();

        map.put("hi", "ryan");

        Request request = mockedContextWithQueryMap(map).request();

        assertEquals("ryan", request.queryParams("hi"));

    }

    @Test
    void testCreateProperFilter() {

        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id_equals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        List<FilterQuery> filterQueries = request.filtersForModel(resourceClass);

        assertEquals(1, filterQueries.size());
        assertEquals("id", filterQueries.get(0).getFilterableField().getName());
        assertEquals("1234", filterQueries.get(0).getFilterValue());
        assertEquals(FilterType.EQUALS, filterQueries.get(0).getFilterType());

    }

    @Test
    void testMongoQuery() {
        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id_equals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        List<FilterQuery> filterQueries = request.filtersForModel(resourceClass);

        assertEquals(1, filterQueries.size());
    }

    @Test
    void testFilterDoesntTryToCreateWithWrongParam() {
        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id_quals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        List<FilterQuery> filterQueries = request.filtersForModel(resourceClass);

        assertEquals(0, filterQueries.size());


    }

    @Test
    void testThrowExceptionWithInvalidPropName() {
        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id33_Ee_equals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        ElepyException elepyException = assertThrows(ElepyException.class, () -> request.filtersForModel(resourceClass));


        assertTrue(elepyException.getMessage().contains("id33_Ee"));


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
