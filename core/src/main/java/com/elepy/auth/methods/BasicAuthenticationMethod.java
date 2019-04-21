package com.elepy.auth.methods;

import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import com.elepy.http.Request;

import java.util.Optional;

public class BasicAuthenticationMethod implements AuthenticationMethod {
    private final UserLoginService userService;

    public BasicAuthenticationMethod(UserLoginService userService) {
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
