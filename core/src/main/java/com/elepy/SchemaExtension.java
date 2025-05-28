package com.elepy;

import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.http.HttpService;
import com.elepy.schemas.SchemaRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SchemaExtension implements ElepyExtension {

    @Inject
    private SchemaRegistry schemaRegistry;

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {

        http.get("/elepy/schemas", ctx -> {
            ctx.response().json(schemaRegistry.getSchemas());
        });
    }
}
