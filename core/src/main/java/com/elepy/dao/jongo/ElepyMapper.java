package com.elepy.dao.jongo;

import com.elepy.dao.Crud;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.jongo.Mapper;
import org.jongo.ObjectIdUpdater;
import org.jongo.marshall.Marshaller;
import org.jongo.marshall.Unmarshaller;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.query.QueryFactory;

public class ElepyMapper implements Mapper {
    private final Mapper defaultMapper;

    private final ElepyIdUpdater elepyIdUpdater;
    private final Crud crud;

    public ElepyMapper(Crud crud) {
        this.crud = crud;
        elepyIdUpdater = new ElepyIdUpdater(crud);

        final JacksonMapper.Builder builder = new JacksonMapper.Builder();

        builder.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        builder.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        defaultMapper = builder.build();
    }

    @Override
    public Marshaller getMarshaller() {
        return defaultMapper.getMarshaller();
    }

    @Override
    public Unmarshaller getUnmarshaller() {
        return defaultMapper.getUnmarshaller();
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
