package com.elepy.auth;


import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;

public class UserAuthenticationExtension implements ElepyExtension {

    @Inject
    private UserCenter userCrud;


    @Inject
    private AuthenticationService authenticationService;


    @Inject
    private ObjectMapper objectMapper;

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {

        http.get("/elepy/has-users", ctx -> {
            if (userCrud.hasUsers()) {
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

            final var grant = ctx.request().grant().orElseThrow(ElepyException::notAuthorized);
            final var user = ctx.request().loggedInUser().orElseThrow(ElepyException::notAuthorized);

            final var grantTree = objectMapper.valueToTree(grant);
            final var userTree = objectMapper.valueToTree(user);

            final var merged = objectMapper.readerForUpdating(grantTree).readValue(userTree);

            ctx.response().json(merged);
            ctx.response().header("Vary", "*");
        });

        http.post("/elepy/token-login", ctx -> {
            final var token = authenticationService.generateToken(ctx.request());

            ctx.response().status(200);

            ctx.response().cookie("ELEPY_TOKEN", token);
            ctx.response().json(token);
        });
    }


}
