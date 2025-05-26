package com.elepy;

import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.http.HttpService;

public class SchemaExtension implements ElepyExtension {
    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {

        http.get("/elepy/schemas", ctx -> {
            ctx.response().json(elepy.modelSchemas());
        });
    }
}
