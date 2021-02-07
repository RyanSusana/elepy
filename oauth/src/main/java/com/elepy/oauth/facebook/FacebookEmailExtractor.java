package com.elepy.oauth.facebook;

import com.elepy.exceptions.ElepyException;
import com.elepy.oauth.EmailExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class FacebookEmailExtractor implements EmailExtractor {
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v9.0/me";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String getEmail(ObjectNode accessToken) throws InterruptedException, ExecutionException, IOException {
        final var request = HttpRequest.newBuilder(URI.create(PROTECTED_RESOURCE_URL + "?fields=email&access_token=" + accessToken.get("access_token").asText())).GET()
                .build();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw ElepyException.of("Bad Facebook request");
        }
        final var profile = (ObjectNode) objectMapper.readTree(response.body());

        return profile.get("email").asText();
    }
}
