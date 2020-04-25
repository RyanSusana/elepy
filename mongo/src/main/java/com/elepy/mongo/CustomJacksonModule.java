package com.elepy.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mongojack.internal.MongoJackModule;

public class CustomJacksonModule extends MongoJackModule {

    public static ObjectMapper configure(ObjectMapper objectMapper) {
        objectMapper.registerModule(new CustomJacksonModule());
        return objectMapper;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.insertAnnotationIntrospector(new CustomAnnotationIntrospector(context.getTypeFactory()));
    }
}
