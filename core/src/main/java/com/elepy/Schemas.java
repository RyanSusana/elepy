package com.elepy;

import com.elepy.models.Schema;
import com.elepy.utils.ModelUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Schemas {
    private final List<Schema<?>> schemas = new ArrayList<>();


    public void addSchema(Schema<?> schema) {
        schemas.add(schema);
    }

    public <T> Schema<T> getSchema(Class<T> cls) {
        var schema = schemas.stream().filter(s -> cls.equals(s.getJavaClass())).findFirst();
        if (schema.isEmpty()) {
            Schema<T> deepSchema = ModelUtils.createDeepSchema(cls);
            addSchema(deepSchema);
            return deepSchema;
        }

        return (Schema<T>) schema.get();


    }
}
