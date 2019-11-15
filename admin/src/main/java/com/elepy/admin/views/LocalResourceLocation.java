package com.elepy.admin.views;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpService;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class LocalResourceLocation implements ResourceLocation, ElepyExtension {

    private static final String JS_LOCATION = "/admin/resources/ElepyVue.js";
    private static final String CSS_LOCATION = "/admin/resources/ElepyVue.css";
    
    private InputStream getResource(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
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
        try (var cssStream = getResource("frontend/dist/ElepyVue.css");
             var jsStream = getResource("frontend/dist/ElepyVue.umd.min.js")) {
            var css = IOUtils.toByteArray(cssStream);
            var js = IOUtils.toByteArray(jsStream);

            http.get(CSS_LOCATION, ctx -> {
                ctx.response().type("text/css");
                ctx.response().result(css);
            });

            http.get(JS_LOCATION, ctx -> {
                ctx.response().type("text/javascript");
                ctx.response().result(js);
            });
        } catch (IOException | NullPointerException e) {
            throw new ElepyConfigException("Error loading static resources", e);
        }

    }
}
