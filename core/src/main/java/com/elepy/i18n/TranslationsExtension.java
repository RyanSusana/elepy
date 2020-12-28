package com.elepy.i18n;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.http.HttpService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TranslationsExtension implements ElepyExtension {
    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        final var propsMapper = JavaPropsMapper.builder().build();
        http.get("/elepy/translations", context -> {
            final var bundle = elepy.getDependency(Resources.class).getResourceBundle(context.locale());
            context.response().json(propsMapper.readPropertiesAs(getResourceBundleAsProperties(bundle), JsonNode.class));
        });
    }

    public static Properties getResourceBundleAsProperties(ResourceBundle resourceBundle) {
        return resourceBundle.keySet()
                .stream()
                .collect(Collectors
                        .toMap(Function.identity(),
                                resourceBundle::getObject,
                                (a, b) -> b,
                                Properties::new)
                );
    }
}
