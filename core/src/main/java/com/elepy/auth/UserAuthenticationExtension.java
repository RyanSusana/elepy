package com.elepy.auth;


import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import com.elepy.http.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserAuthenticationExtension implements ElepyExtension {

    @Inject
    private Crud<User> userCrud;

    private List<AuthenticationMethod> authenticationMethods = new ArrayList<>();


    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {

        authenticationMethods.forEach(elepy::injectFields);

        http.get("/elepy-login-check", ctx -> {
            ctx.loggedInUserOrThrow();
            ctx.result(Message.of("Your are logged in", 200));
        });

        http.post("/elepy-token-login", (request, response) -> {

            boolean keepLoggedIn = Boolean.parseBoolean(request.queryParamOrDefault("keepLoggedIn", "false"));

            int durationInSeconds = keepLoggedIn ? 30 * 60 * 60 * 24 : 60 * 60;
            final var token = generateToken(request, durationInSeconds * 1000);

            response.status(200);


            response.cookie("ELEPY_TOKEN", token);
            response.json(token);
        });
    }

    public void addAuthenticationMethod(AuthenticationMethod authHandler) {
        if (authHandler instanceof TokenAuthenticationMethod) {
            authenticationMethods.add(0, authHandler);
        } else {
            authenticationMethods.add(authHandler);
        }
    }

    public void tryToLogin(Request request) {
        if (request.attribute("user") == null) {
            request.attribute("user", authenticateUser(request).orElse(null));
        }
    }


    private String generateToken(Request request, int duration) {
        final var user1 = authenticateUser(request);
        return user1.map(user -> getTokenAuthenticationMethod().orElseThrow(() -> new ElepyException("Only Basic Authentication supported")).createToken(user, duration))
                .orElseThrow(() -> new ElepyException("Credentials invalid", 401));
    }

    private Optional<User> authenticateUser(Request request) {
        for (AuthenticationMethod authenticationMethod : authenticationMethods) {
            final var user = authenticationMethod.authenticateUser(request);

            if (user.isPresent()) {
                return user;
            }
        }
        return Optional.empty();
    }

    public Optional<TokenAuthenticationMethod> getTokenAuthenticationMethod() {
        return authenticationMethods.stream()
                .filter(authenticationMethod -> authenticationMethod instanceof TokenAuthenticationMethod)
                .findFirst()
                .map(authenticationMethod -> (TokenAuthenticationMethod) authenticationMethod);
    }
}
