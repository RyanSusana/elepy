package com.elepy.auth.authentication.methods.basic;

import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authentication.AuthenticationMethod;
import com.elepy.auth.users.UserService;
import com.elepy.http.Request;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped
public class BasicAuthenticationMethod implements AuthenticationMethod {

    @Inject
    private UserService userService;

    @Override
    public Optional<Credentials> getCredentials(Request request) {

        final Optional<String[]> authorizationOpt = this.basicCredentials(request);
        if (authorizationOpt.isEmpty()) {
            return Optional.empty();
        }

        final String[] authorization = authorizationOpt.get();

        return userService.login(authorization[0], authorization[1])
                .map(userService::getCredentialsForUser);
    }


    private Optional<String[]> basicCredentials(Request request) {
        String header = request.headers("Authorization");

        if (header == null || !header.startsWith("Basic")) {
            return Optional.empty();
        }

        var strippedDownHeader = header.replaceAll("Basic", "").trim();
        final String[] strings = readBasicUsernamePassword(strippedDownHeader);

        if (strings.length != 2) {
            return Optional.empty();
        }
        return Optional.of(strings);

    }

    private String[] readBasicUsernamePassword(String base64) {
        String credentials = new String(Base64.getDecoder().decode(base64),
                StandardCharsets.UTF_8);
        return credentials.split(":", 2);
    }


}
