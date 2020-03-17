package com.elepy.admin;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.http.HttpService;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Resource> getResources(String dir, String pathOnServer, String contentType, String extension) throws IOException, URISyntaxException {
        return listResourcesInDir(dir, extension).map(resource -> {
            try {
                final var resourceLocation = dir + resource;
                return new Resource(
                        pathOnServer.replace(":name", resource),
                        contentType,
                        IOUtils.toByteArray(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceLocation)))
                );

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Stream<String> listResourcesInDir(String dir, String extension) throws IOException, URISyntaxException {
       return Arrays.stream(getResourceListing(getClass(), dir)).filter(s -> s.endsWith(extension));
    }

    //cleanup
    static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }


}
