package com.elepy.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RawJsonSerializer extends JsonSerializer<String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.setCodec(objectMapper);
        gen.writeTree(objectMapper.readTree(value));
    }
}
