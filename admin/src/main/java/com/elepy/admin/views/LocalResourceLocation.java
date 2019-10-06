package com.elepy.admin.views;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.http.HttpService;

import java.io.IOException;
import java.io.InputStream;

public class LocalResourceLocation implements ResourceLocation, ElepyExtension {

    public static final String JS_LOCATION = "/admin/resources/ElepyVue.js";
    public static final String CSS_LOCATION = "/admin/resources/ElepyVue.css";
    private final byte[] css, js;


    public LocalResourceLocation() {
        try (var cssStream = getResource("frontend/dist/ElepyVue.css");
             var jsStream = getResource("frontend/dist/ElepyVue.umd.min.js")) {
            this.css = cssStream.readAllBytes();
            this.js = jsStream.readAllBytes();
        } catch (IOException | NullPointerException e) {

            throw new ElepyConfigException("Error loading Static Resources", e);
        }
    }

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
