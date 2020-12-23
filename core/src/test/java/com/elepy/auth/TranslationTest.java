package com.elepy.auth;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Pattern;

public class TranslationTest {

    @Test
    void name() {
        final var resourceBundle = Map.of("ryan", "susana",
                "jeffrey", "lobato",
                "bros", "{ryan} and {jeffrey}");
        String charSequence = "Bros: {bros}";


        System.out.println(interpolate(charSequence, resourceBundle));


    }

    String interpolate(String input, Map<String, String> resourceBundle) {
        final var pattern = Pattern.compile("\\{\\W*([\\w\\\\.]+)\\W*}");

        final var matcher = pattern.matcher(input);

        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            final var replacement = resourceBundle.get(matcher.group(1));
            matcher.appendReplacement(stringBuilder, replacement);
        }

        final var interpolatedInput = stringBuilder.toString();
        if (!pattern.matcher(interpolatedInput).find()) {
            return interpolatedInput;
        }

        return interpolate(interpolatedInput, resourceBundle);
    }

}
