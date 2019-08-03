package com.elepy.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.bson4jackson.MongoBsonFactory;

public class JongoMapperFactory {

    public static Mapper createMapper() {
        final var oMapper = new ObjectMapper(MongoBsonFactory.createFactory());

        return new JacksonMapper.Builder().withObjectIdUpdater(new ElepyIdUpdater(oMapper)).build();
    }
} 
