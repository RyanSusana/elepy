package com.elepy.json;

import com.elepy.exceptions.TranslatedMessage;
import com.elepy.i18n.Resources;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class TranslatableSerializer extends JsonSerializer<Object> {


    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var resourceBundleLocator = (ResourceBundleLocator) serializers.getConfig().getAttributes().getAttribute("resourceBundleLocator");

        if (resourceBundleLocator == null) {
            resourceBundleLocator = new Resources();
        }
        final var locale = serializers.getConfig().getLocale();
        final var resourceBundle = resourceBundleLocator.getResourceBundle(locale);

        if (value instanceof TranslatedMessage) {
            gen.writeString(interpolate((TranslatedMessage) value, resourceBundle));
        } else {
            gen.writeString(interpolate(value.toString(), resourceBundle));
        }
    }

    private String interpolate(TranslatedMessage translatedMessage, ResourceBundle resourceBundle) {

        String interpolated;
        try {
            interpolated = resourceBundle.getString(translatedMessage.getMessageTemplate());
        } catch (MissingResourceException e) {
            interpolated = translatedMessage.getMessageTemplate();
        }

        final var messageFormat = new MessageFormat(interpolated, resourceBundle.getLocale());
        return messageFormat.format(translatedMessage.getInput());
    }

    private String interpolate(String input, ResourceBundle resourceBundle) {
        final var pattern = Pattern.compile("\\{\\W*([a-zA-Z]+[\\w\\\\.]*)\\W*}");

        final var matcher = pattern.matcher(input);

        StringBuilder stringBuilder = new StringBuilder();
        if (!matcher.find()) {
            return input;
        }

        matcher.reset();
        while (matcher.find()) {

            final var group = matcher.group(1);
            matcher.appendReplacement(stringBuilder, Optional.ofNullable(resourceBundle)
                    .map(rb -> rb.getString(group))
                    .orElse(group));

        }

        final var interpolatedInput = stringBuilder.toString();
        if (!pattern.matcher(interpolatedInput).find()) {
            return interpolatedInput;
        }

        return interpolate(interpolatedInput, resourceBundle);
    }


}
