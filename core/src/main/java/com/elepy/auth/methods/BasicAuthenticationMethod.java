package com.elepy.auth.methods;

import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;
import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.User;
import com.elepy.auth.UserLoginService;
import com.elepy.http.Request;

import java.util.Optional;

public class BasicAuthenticationMethod implements AuthenticationMethod {

    private final UserLoginService userService;

    @ElepyConstructor
    public BasicAuthenticationMethod(@Inject UserLoginService userService) {
        this.userService = userService;
    }

    public User getUserFromRequest(Request request) {

        final Optional<String[]> authorizationOpt = this.basicCredentials(request);
        if (authorizationOpt.isEmpty()) {
            return null;
        }

        final String[] authorization = authorizationOpt.get();
        final Optional<User> login = userService.login(authorization[0], authorization[1]);

        return login.orElse(null);

    }


}
