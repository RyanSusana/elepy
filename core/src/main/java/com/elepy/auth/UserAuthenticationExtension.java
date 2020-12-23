package com.elepy.auth;


import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.annotations.Inject;
import com.elepy.auth.methods.BasicAuthenticationMethod;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import com.elepy.http.Request;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class UserAuthenticationExtension implements ElepyExtension {

    @Inject
    private Crud<User> userCrud;


    @Inject
    private BasicAuthenticationMethod basicAuthenticationMethod;


    private TokenGenerator tokenGenerator;

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {

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

            final var grant = ctx.request().grant().orElseThrow(() -> new ElepyException("Must be logged in.", 401));
            final var user = ctx.request().loggedInUser().orElseThrow(() -> new ElepyException("Must be logged in.", 401));

            final var grantTree = objectMapper.valueToTree(grant);
            final var userTree = objectMapper.valueToTree(user);

            final var merged = objectMapper.readerForUpdating(grantTree).readValue(userTree);

            ctx.response().json(merged);
            ctx.response().header("Vary", "*");
        });

        http.post("/elepy/token-login", ctx -> {
            final var token = generateToken(ctx.request());

            ctx.response().status(200);

            ctx.response().cookie("ELEPY_TOKEN", token);
            ctx.response().json(token);
        });
    }

    public boolean hasTokenGenerator() {
        return tokenGenerator != null;
    }

    public void setTokenGenerator(TokenGenerator authHandler) {
        this.tokenGenerator = authHandler;
    }

    public Optional<Grant> getGrant(Request request) {
        final Grant grantFromRequest = request.attribute("grant");
        if (grantFromRequest != null) {
            return Optional.of(grantFromRequest);
        } else {
            final var grantMaybe = authenticate(request, List.of(basicAuthenticationMethod, tokenGenerator));

            grantMaybe.ifPresent(g -> request.attribute("grant", g));
            return grantMaybe;
        }
    }


    private String generateToken(Request request) {
        final Optional<Grant> grant = authenticate(request, List.of(basicAuthenticationMethod));
        return grant
                .map(grant1 -> {
                    grant1.setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
                    return grant1;
                }).map(grant1 -> tokenGenerator
                        .createToken(grant1))
                .orElseThrow(() -> new ElepyException("Credentials invalid", 401));
    }

    private Optional<Grant> authenticate(Request request, List<AuthenticationMethod> authenticationMethods) {
        for (AuthenticationMethod authenticationMethod : authenticationMethods) {
            final var user = authenticationMethod.getGrant(request);

            if (user.isPresent()) {
                return user;
            }
        }
        return Optional.empty();
    }


}
