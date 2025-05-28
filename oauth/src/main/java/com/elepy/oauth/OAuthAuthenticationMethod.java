package com.elepy.oauth;

import com.elepy.auth.authentication.AuthenticationMethod;
import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.users.User;
import com.elepy.auth.users.UserService;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuthRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class OAuthAuthenticationMethod implements AuthenticationMethod {

    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(new OAuthParamNamingStrategy());

    @Override
    public Optional<Credentials> getCredentials(Request request) {
        try {
            final var userCenter = request.elepy().getDependency(UserService.class);
            final var code = request.queryParams("code");
            final var services = request.elepy().getDependency(AuthSchemes.class);

            final var state = objectMapper.readValue(request.queryParamOrDefault("state", "{}"), new TypeReference<Map<String, String>>() {
            });


            final var scheme = services.getServiceWrapper(state.get("scheme"));

            if (code == null || scheme == null) {
                return Optional.empty();
            }

            final var accessTokenResponse = scheme.getService().execute(createAccessTokenRequest(scheme, code, state.get("redirect_uri")));

            if (!accessTokenResponse.isSuccessful()) {
                throw ElepyException.internalServerError(new RuntimeException("Unsuccessful OAuthToken request"));
            }
            final Object extract = scheme.getService().getApi().getAccessTokenExtractor().extract(accessTokenResponse);
            final var email = scheme.getEmailExtractor().getEmail(objectMapper.valueToTree(extract));
            final User user;
            if (!userCenter.hasUsers()) {
                user = createOwner(email);
                userCenter.users().create(user);
            } else {
                // TODO Auto sign-up?
                user = userCenter.getUserByUsername(email).orElseThrow(ElepyException::notAuthorized);
            }

            return Optional.ofNullable(userCenter.getCredentialsForUser(user));
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }


    }


    protected OAuthRequest createAccessTokenRequest(AuthScheme scheme, String code, String redirectUri) {
        final var api = scheme.getService().getApi();
        OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        api.getClientAuthentication().addClientAuthentication(request, scheme.getService().getApiKey(), scheme.getService().getApiSecret());
        request.addParameter("code", code);
        request.addParameter("redirect_uri", redirectUri);

        request.addParameter("scope", scheme.getService().getDefaultScope());

        request.addParameter("grant_type", "authorization_code");
        return request;
    }

    private User createOwner(String email) {
        final var user = new User();

        user.setUsername(email);
        user.setId(UUID.randomUUID().toString());
        user.setPassword(null);

        user.cleanUsername();
        return user;
    }


}
