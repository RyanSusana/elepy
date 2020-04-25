package com.elepy.mongo;

import com.elepy.annotations.Identifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mongojack.internal.MongoJackModule;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonTest {

    @Test
    void theTest() throws IOException {
        // Without MongoJack
        final var mapper = CustomJacksonModule.configure(new ObjectMapper());

        String test = "{\"_id\":\"arbitrary data\"}";

        final var testPojo = mapper.readValue(test, TestPojo.class);

        mapper.reader().forType(TestPojo.class);
        assertEquals("arbitrary data", testPojo.theId);
    }

    public static class TestPojo {
        @Identifier
        private String theId = "test";

    }
} 
