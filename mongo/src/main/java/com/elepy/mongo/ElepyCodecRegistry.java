package com.elepy.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.mongojack.JacksonCodecRegistry;
import org.mongojack.internal.stream.JacksonCodec;
import org.mongojack.internal.stream.JacksonDecoder;
import org.mongojack.internal.stream.JacksonEncoder;
import org.mongojack.internal.util.DocumentSerializationUtils;

import java.util.concurrent.ConcurrentHashMap;

public class ElepyCodecRegistry extends JacksonCodecRegistry {

    private final ObjectMapper objectMapper;
    private final Class<?> view;
    private final ConcurrentHashMap<Class<?>, Codec<?>> codecCache = new ConcurrentHashMap<>();

    public ElepyCodecRegistry(ObjectMapper objectMapper, Class<?> view) {
        super(objectMapper, view);
        this.objectMapper = objectMapper;
        this.view = view;
    }

    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return this.addCodecForClass(clazz);
//        return !DocumentSerializationUtils.isKnownClass(clazz) && !DBObject.class.isAssignableFrom(clazz) && !Document.class.isAssignableFrom(clazz) && !Bson.class.isAssignableFrom(clazz) ? this.addCodecForClass(clazz) : null;
    }
    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> addCodecForClass(Class<T> clazz) {
        return (Codec<T>) this.codecCache.computeIfAbsent(clazz, (k) -> {
            JacksonEncoder<T> encoder = new JacksonEncoder(clazz, this.view, this.objectMapper);
            JacksonDecoder<T> decoder = new JacksonDecoder(clazz, this.view, this.objectMapper);
            return new ElepyCodec<>(encoder, decoder);
        });
    }
}
