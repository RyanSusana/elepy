package com.elepy.auth.extension;


import com.elepy.auth.authentication.AuthenticationService;
import com.elepy.auth.users.UserCenter;
import com.elepy.configuration.ElepyExtension;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.exceptions.ElepyException;
import com.elepy.exceptions.Message;
import com.elepy.http.HttpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserAuthenticationExtension implements ElepyExtension {


    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {
        var userCrud = elepy.getDependency(UserCenter.class);

        var authenticationService = elepy.getDependency(AuthenticationService.class);
        var objectMapper = elepy.getDependency(ObjectMapper.class);
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

            final var grant = ctx.request().loggedInCredentials().orElseThrow(ElepyException::notAuthorized);
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
