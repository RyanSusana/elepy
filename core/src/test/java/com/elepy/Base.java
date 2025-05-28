package com.elepy;

import com.elepy.http.HttpContext;
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

        resource.setNonEditable("nonEditable");

        return resource;
    }


    protected HttpContext mockedContextWithQueryMap(Map<String, String> map) {
        HttpContext mockedContext = mockedContext();
        when(mockedContext.request().queryParams()).thenReturn(map.keySet());

        when(mockedContext.request().queryParams(anyString())).thenAnswer(invocationOnMock -> map.get(invocationOnMock.getArgument(0)));

        return mockedContext;
    }

    protected HttpContext mockedContext() {
        HttpContext context = mock(HttpContext.class);
        Request request = mock(Request.class);
        Response response = mock(Response.class);


        when(context.response()).thenReturn(response);
        when(context.request()).thenReturn(request);


        when(request.loggedInUser()).thenCallRealMethod();


        return context;
    }

    protected Elepy createElepy() {
        final int port = portCounter++;
        return new Elepy()
                .withHttpService(MockHttpService.class)
                .configureHttp(http -> {
                    http.port(port);
                })
                .withDefaultCrudFactory(EmptyCrudFactory.class);

    }

}
