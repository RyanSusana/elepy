package com.elepy.mongo;

import com.elepy.annotations.Identifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonTest {

    @Test
    void testIdentifierHasCorrectJacksonAlias() throws IOException {
        // Without MongoJack
        final var mapper = CustomJacksonModule.configure(new ObjectMapper());

        String test = "{\"_id\":\"arbitrary data\"}";

        final var testPojo = mapper.readValue(test, TestPojo.class);

        final var theId = testPojo.theId;
        System.out.println(theId);
        assertEquals("arbitrary data", testPojo.theId);
    }

    public static class TestPojo {
        @Identifier
        protected String theId = "test";

    }
} 
