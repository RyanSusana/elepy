package com.elepy.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class TranslatableDeserializer extends JsonDeserializer<Map<Locale, ?>> {
    @Override
    public Map<Locale, ?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        return null;
    }
}
