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

        http.get("/elepy/has-users", ctx -> {
            final var count = userCrud.count();

            if (count > 0) {
                ctx.result(Message.of("Users exist", 200));
            } else {
                ctx.result(Message.of("No users exist", 404));
            }
        });
        http.get("/elepy/login-check", ctx -> {
            ctx.loggedInUserOrThrow();
            ctx.result(Message.of("Your are logged in", 200));
            ctx.response().header("Vary", "*");
        });

        http.get("/elepy/logged-in-user", ctx -> {
            ctx.response().json(userCrud.getById(ctx.loggedInUserOrThrow().getId()).orElseThrow().withEmptyPassword());
            ctx.response().header("Vary", "*");
        });

        http.post("/elepy/token-login", (request, response) -> {
            final var token = generateToken(request);

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

    public Optional<Grant> getGrant(Request request) {
        final Grant grantFromRequest = request.attribute("grant");
        if (grantFromRequest != null) {
            return Optional.of(grantFromRequest);
        } else {
            final var grantMaybe = authenticateUser(request);

            grantMaybe.ifPresent(g -> request.attribute("grant", g));
            return grantMaybe;
        }
    }


    private String generateToken(Request request) {
        final Optional<Grant> grant = authenticateUser(request);
        return grant.map(user -> getTokenAuthenticationMethod()
                .orElseThrow(() -> new ElepyException("Only Basic Authentication supported"))
                .createToken(user))
                .orElseThrow(() -> new ElepyException("Credentials invalid", 401));
    }

    private Optional<Grant> authenticateUser(Request request) {
        for (AuthenticationMethod authenticationMethod : authenticationMethods) {
            final var user = authenticationMethod.getGrant(request);

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
