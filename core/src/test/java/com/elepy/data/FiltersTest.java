package com.elepy.data;

import com.elepy.Resource;
import com.elepy.query.Filter;
import com.elepy.query.FilterType;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpContext;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.schemas.SchemaFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled("Replace with better mocking of Request and Response")
public class FiltersTest {

    @Test
    void testProperTestSetup() {
        Map<String, String> map = new HashMap<>();

        map.put("hi", "ryan");
        Request request = mockedContextWithQueryMap(map).request();
        assertThat(request.queryParams("hi")).isEqualTo("ryan");
    }

    @Test
    void testCreateProperFilter() {

        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id_equals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        List<Filter> filterQueries = request.filtersForModel(resourceClass);

        assertThat(filterQueries.size()).isEqualTo(1);
        assertThat(filterQueries.get(0).getPropertyName()).isEqualTo("id");
        assertThat(filterQueries.get(0).getFilterValue()).isEqualTo("1234");
        assertThat(filterQueries.get(0).getFilterType()).isEqualTo(FilterType.EQUALS);

    }

    @Test
    void testMongoQuery() {
        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id_equals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        List<Filter> filterQueries = request.filtersForModel(resourceClass);

        assertThat(filterQueries.size()).isEqualTo(1);
    }

    @Test
    void testFilterDoesntTryToCreateWithWrongParam() {
        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id_quals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        List<Filter> filterQueries = request.filtersForModel(resourceClass);

        assertThat(filterQueries.size()).isEqualTo(0);


    }

    @Test
    @Disabled("Change the way queries are validated at DAO level")
    void testThrowExceptionWithInvalidPropName() {
        Class<Resource> resourceClass = Resource.class;

        Map<String, String> map = new HashMap<>();

        map.put("id33_Ee_equals", "1234");

        Request request = mockedContextWithQueryMap(map).request();

        assertThatExceptionOfType(ElepyException.class)
                .isThrownBy(() -> request.filtersForModel(resourceClass))
                .withMessageContaining("id33_Ee");
    }

    private HttpContext mockedContextWithQueryMap(Map<String, String> map) {
        HttpContext mockedContext = mockedContext();
        when(mockedContext.request().attribute(any())).thenReturn(List.of(new SchemaFactory().createDeepSchema(Resource.class)));
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
