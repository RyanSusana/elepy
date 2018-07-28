package com.elepy.admin.concepts.auth;

import com.elepy.admin.models.User;
import com.elepy.admin.services.UserService;
import spark.Request;

import java.util.Optional;

public class BasicHandler implements AuthHandler {
    private final UserService userService;

    public BasicHandler(UserService userService) {
        this.userService = userService;
    }

    public User login(Request request) {

        final Optional<String[]> authorizationOpt = this.basicCredentials(request);
        if (!authorizationOpt.isPresent()) {
            return null;
        }

        final String[] authorization = authorizationOpt.get();
        final Optional<User> login = userService.login(authorization[0], authorization[1]);

        return login.orElse(null);

    }


}
