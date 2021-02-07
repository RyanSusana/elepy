package com.elepy.oauth.openid;

import com.elepy.exceptions.ElepyException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.openid.OpenIdJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class OpenIDApi extends DefaultApi20 {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Object> wellKnownConfiguration;

    private final String wellKnownUrl;

    public OpenIDApi(String wellKnownUrl) {
        this.wellKnownUrl = wellKnownUrl;
    }

    private synchronized Map<String, Object> fetchConfiguration() {

        final var request = HttpRequest.newBuilder().uri(URI.create(wellKnownUrl)).GET().header("Content-Type", "application/json").build();
        try {
            final var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw ElepyException.of("Can't find OpenID Connect Configuration", 500);
            }
            final var body = response.body();
            return objectMapper.readValue(body, new TypeReference<>() {
            });
        } catch (IOException | InterruptedException e) {
            throw ElepyException.internalServerError(e);
        }
    }

    private String get(String prop) {
        if (wellKnownConfiguration == null) {
            wellKnownConfiguration = fetchConfiguration();
        }
        return wellKnownConfiguration.get(prop).toString();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return get("token_endpoint");
    }


    @Override
    protected String getAuthorizationBaseUrl() {
        return get("authorization_endpoint");
    }


    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OpenIdJsonTokenExtractor.instance();
    }


    @Override
    public String getRevokeTokenEndpoint() {
        return get("revocation_endpoint");
    }


    @Override
    public String getDeviceAuthorizationEndpoint() {
        return get("device_authorization_endpoint");
    }

}
