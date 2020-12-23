package com.elepy.json;

import com.elepy.di.ElepyContext;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class TranslatableSerializer extends JsonSerializer<String> {


    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final var locale = serializers.getLocale();

        final var elepyContext = (ElepyContext) serializers.getConfig().getAttributes().getAttribute("elepyContext");
        final var resourceBundle = elepyContext
                .getDependency(ResourceBundleLocator.class)
                .getResourceBundle(locale);


        gen.writeString(resourceBundle.getString(value));

    }



}
