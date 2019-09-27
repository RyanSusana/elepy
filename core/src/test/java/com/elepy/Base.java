package com.elepy;

import com.elepy.http.HttpContext;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.elepy.http.Response;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class Base {

    private static int objectCounter = -100;
    private static int portCounter = 4000;

    public synchronized Resource validObject() {
        Resource resource = new Resource();

        resource.setId(objectCounter++);
        resource.setMaxLen40("230428");
        resource.setMinLen20("My name is ryan and this is a string  with more than 20 chars");
        resource.setMinLen10MaxLen50("12345678910111213");
        resource.setNumberMax40(BigDecimal.valueOf(40));
        resource.setNumberMin20(BigDecimal.valueOf(20));
        resource.setNumberMin10Max50(BigDecimal.valueOf(15));
        resource.setUnique("unique");
        resource.setMARKDOWN("MARKDOWN");
        resource.setTextArea("textarea");
        resource.setTextField("textfield");
        resource.setSearchableField("searchable");
        resource.setRequired("required");

        resource.setNonEditable("nonEditable");

        return resource;
    }


    protected HttpContext mockedContextWithQueryMap(Map<String, String> map) {
        HttpContext mockedContext = mockedContext();
        when(mockedContext.request().queryParams()).thenReturn(map.keySet());

        when(mockedContext.request().queryParams(anyString())).thenAnswer(invocationOnMock -> map.get(invocationOnMock.getArgument(0)));

        when(mockedContext.request().filtersForModel(any())).thenCallRealMethod();
        return mockedContext;
    }

    protected HttpContext mockedContext() {
        HttpContext context = mock(HttpContext.class);
        Request request = mock(Request.class);
        Response response = mock(Response.class);


        when(context.response()).thenReturn(response);
        when(context.request()).thenReturn(request);
        when(request.permissions()).thenCallRealMethod();

        doCallRealMethod().when(context).requirePermissions(anyCollection());
        doCallRealMethod().when(context).requirePermissions(any(String.class));
        doCallRealMethod().when(context).requirePermissions(any(String[].class));

        when(context.hasPermissions(anyCollection())).thenCallRealMethod();
        when(request.loggedInUser()).thenCallRealMethod();

        doCallRealMethod().when(request).requirePermissions(anyCollection());
        doCallRealMethod().when(request).hasPermissions(any());
        doCallRealMethod().when(request).requirePermissions(any(String.class));
        doCallRealMethod().when(request).requirePermissions(any(String[].class));

        return context;
    }

    protected Elepy createElepy() {

        return new Elepy().onPort(portCounter++)
                .withHttpService(mock(HttpService.class))
                .withDefaultCrudFactory(new MockCrudFactory());


    }

}
