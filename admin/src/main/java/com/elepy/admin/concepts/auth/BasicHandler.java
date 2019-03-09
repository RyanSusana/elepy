package com.elepy.admin.concepts.auth;

import com.elepy.admin.models.UserInterface;
import com.elepy.admin.services.UserService;
import com.elepy.http.Request;

import java.util.Optional;

public class BasicHandler implements AuthHandler {
    private final UserService userService;

    public BasicHandler(UserService userService) {
        this.userService = userService;
    }

    public UserInterface login(Request request) {

        final Optional<String[]> authorizationOpt = this.basicCredentials(request);
        if (!authorizationOpt.isPresent()) {
            return null;
        }

        final String[] authorization = authorizationOpt.get();
        final Optional<UserInterface> login = userService.login(authorization[0], authorization[1]);

        return login.orElse(null);

    }


}
