package com.elepy.oauth.github;

import com.elepy.exceptions.ElepyException;
import com.elepy.oauth.EmailExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

public class GitHubEmailExtractor implements EmailExtractor {

    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user/emails";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String getEmail(ObjectNode accessTokenResponse) throws InterruptedException, ExecutionException, IOException {
        final var request = HttpRequest.newBuilder(URI.create(PROTECTED_RESOURCE_URL)).GET()
                .header("Authorization", "Bearer " + accessTokenResponse.get("access_token").asText())
                .header("Accept", "application/vnd.github.v3+json")
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw ElepyException.of("Bad GitHub request");
        }
        final var emails = (ArrayNode) objectMapper.readTree(response.body());

        final var primaryEmail = StreamSupport
                .stream(emails.spliterator(), false)
                .map(jsonNode -> (ObjectNode) jsonNode)
                .filter(objectNode -> objectNode.get("primary").asBoolean())
                .findFirst()
                .orElseThrow(ElepyException::notFound);

        if (!primaryEmail.get("verified").asBoolean()) {
            throw ElepyException.of("Email not verified");
        }
        return primaryEmail.get("email").asText();
    }
}
