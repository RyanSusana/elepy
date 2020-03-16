package com.elepy.admin;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.http.HttpService;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static com.elepy.exceptions.HaltException.halt;

public class FrontendLoader implements ElepyExtension {

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        http.get("/elepy-admin", ctx -> ctx.redirect("/elepy-admin/"));
        try {
            Stream.of(
                    getResources(
                            "frontend/dist/",
                            "/elepy-admin/*",
                            "text/html",
                            "index.html"
                    ),
                    getResources(
                            "frontend/dist/js/",
                            "/js/:name",
                            "text/javascript",
                            ".js"
                    ),
                    getResources(
                            "frontend/dist/css/",
                            "/css/:name",
                            "text/css",
                            ".css"
                    )
            ).flatMap(s -> s)
                    .map(Resource::toRoute)
                    .forEach(http::addRoute);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Resource> getResources(String dir, String pathOnServer, String contentType, String extension) throws IOException {
        return listResourcesInDir(dir, extension).map(resource -> {
            try {
                final var resourceLocation = dir + resource;
                return new Resource(
                        pathOnServer.replace(":name", resource),
                        contentType,
                        IOUtils.toByteArray(Objects.requireNonNull(FrontendLoader.class.getResourceAsStream("/" + resourceLocation)))
                );

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Stream<String> listResourcesInDir(String dir, String extension) throws IOException {
        return IOUtils.readLines(Objects.requireNonNull(FrontendLoader.class.getClassLoader().getResourceAsStream(dir)), StandardCharsets.UTF_8).stream().filter(s -> s.endsWith(extension));
    }


}
