package com.elepy.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.Map;

public class OAuthParamNamingStrategy extends PropertyNamingStrategies.SnakeCaseStrategy {
    private static Map<String, String> subs = Map.of(
            "open_id_token", "id_token"
    );

    @Override
    public String translate(String input) {
        final var snakeCase = super.translate(input);

        return subs.getOrDefault(snakeCase, snakeCase);
    }
}
