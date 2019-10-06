package com.elepy.admin.views;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpService;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LocalResourceLocation implements ResourceLocation, ElepyExtension {

    public static final String JS_LOCATION = "/admin/resources/ElepyVue.js";
    public static final String CSS_LOCATION = "/admin/resources/ElepyVue.css";
    private final byte[] css;
    private final byte[] js;


    public LocalResourceLocation() {
        for (File f : getResourceFolderFiles("frontend/dist")) {
            System.out.println(f);
        }
        try (var cssStream = getResource("/frontend/dist/ElepyVue.css");
             var jsStream = getResource("/frontend/dist/ElepyVue.umd.min.js")) {
            this.css = IOUtils.toByteArray(cssStream);
            this.js = IOUtils.toByteArray(jsStream);
        } catch (IOException | NullPointerException e) {
            throw new ElepyConfigException("Error loading Static Resources", e);
        }
    }

    private static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }
    private InputStream getResource(String name) {
        return this.getClass().getResourceAsStream(name);
    }

    @Override
    public String getCssLocation() {
        return CSS_LOCATION;
    }

    @Override
    public String getJsLocation() {
        return JS_LOCATION;
    }

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        http.get(CSS_LOCATION, ctx -> {
            ctx.response().type("text/css");
            ctx.response().result(css);
        });

        http.get(JS_LOCATION, ctx -> {
            ctx.response().type("text/javascript");
            ctx.response().result(js);
        });
    }
}
