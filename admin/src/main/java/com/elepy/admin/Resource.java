package com.elepy.admin;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpMethod;
import com.elepy.http.Route;
import com.elepy.http.RouteBuilder;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Objects;

public class Resource {
    private final String path;
    private final String contentType;
    private final String fileLocation;


    public Resource(String path, String contentType, String raw) {
        this.path = path;
        this.contentType = contentType;
        this.fileLocation = raw;
    }

    public Route toRoute() {
        return RouteBuilder.anElepyRoute().acceptType(contentType).method(HttpMethod.GET).path(path)
                .route(context -> {
                    context.type(contentType);
                    context.response().header("Content-Encoding", "gzip");
                    final var resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileLocation);

                    if(resourceAsStream == null){
                        throw new ElepyException(String.format("Resource '%s' not found", fileLocation));
                    }
                    context.response().result(resourceAsStream);
                }).build();
    }
}
