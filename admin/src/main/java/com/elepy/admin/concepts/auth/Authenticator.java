package com.elepy.admin.concepts.auth;


import com.elepy.admin.models.User;
import com.elepy.exceptions.RestErrorMessage;
import spark.Request;

import java.util.ArrayList;
import java.util.List;

public class Authenticator {

    private List<AuthHandler> authenticationMethods = new ArrayList<>();


    public Authenticator addAuthenticationMethod(AuthHandler authHandler) {
        authenticationMethods.add(authHandler);
        return this;
    }

    public User authenticate(Request request) {
        for (AuthHandler authenticationMethod : authenticationMethods) {
            final User login = authenticationMethod.login(request);

            if (login != null) {
                return login;
            }
        }
        return null;
    }


}
