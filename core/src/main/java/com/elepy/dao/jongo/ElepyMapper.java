package com.elepy.dao.jongo;

import com.elepy.concepts.IdentityProvider;
import com.elepy.dao.Crud;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jongo.Mapper;
import org.jongo.ObjectIdUpdater;
import org.jongo.marshall.Marshaller;
import org.jongo.marshall.Unmarshaller;
import org.jongo.marshall.jackson.JacksonEngine;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.bson4jackson.BsonModule;
import org.jongo.marshall.jackson.bson4jackson.MongoBsonFactory;
import org.jongo.marshall.jackson.configuration.AnnotationModifier;
import org.jongo.marshall.jackson.configuration.Mapping;
import org.jongo.marshall.jackson.configuration.PropertyModifier;
import org.jongo.query.QueryFactory;

public class ElepyMapper implements Mapper {
    private final JacksonEngine jacksonEngine;

    private final Mapper defaultMapper;
    private final ElepyIdUpdater elepyIdUpdater;

    public ElepyMapper(Crud crud, IdentityProvider identityProvider) {
        this.elepyIdUpdater = new ElepyIdUpdater(crud, identityProvider);

        final ObjectMapper mapper = new ObjectMapper(MongoBsonFactory.createFactory())
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        final Mapping mapping = new Mapping.Builder(mapper)
                .registerModule(new BsonModule())
                .addModifier(new PropertyModifier())
                .addModifier(new AnnotationModifier())
                .build();

        this.jacksonEngine = new JacksonEngine(mapping);
        this.defaultMapper = new JacksonMapper.Builder().build();
    }

    @Override
    public Marshaller getMarshaller() {
        return jacksonEngine;
    }

    @Override
    public Unmarshaller getUnmarshaller() {
        return jacksonEngine;
    }

    @Override
    public ObjectIdUpdater getObjectIdUpdater() {
        return elepyIdUpdater;
    }

    @Override
    public QueryFactory getQueryFactory() {
        return defaultMapper.getQueryFactory();
    }
}
