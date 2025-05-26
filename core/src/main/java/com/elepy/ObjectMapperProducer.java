package com.elepy;

import com.elepy.di.ElepyContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ObjectMapperProducer {
    @Inject
    private ElepyContext context;

    @Produces
    public ObjectMapper createObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        objectMapper.setConfig(objectMapper.getSerializationConfig().withAttribute(ElepyContext.class, context));
        objectMapper.setConfig(objectMapper.getDeserializationConfig().withAttribute(ElepyContext.class, context));
        return objectMapper;

    }
}
