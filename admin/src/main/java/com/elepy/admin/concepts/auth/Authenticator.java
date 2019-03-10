package com.elepy.admin.concepts.auth;


import com.elepy.admin.models.UserInterface;
import com.elepy.http.Request;

import java.util.ArrayList;
import java.util.List;

public class Authenticator {

    private List<AuthHandler> authenticationMethods = new ArrayList<>();


    public Authenticator addAuthenticationMethod(AuthHandler authHandler) {
        authenticationMethods.add(authHandler);
        return this;
    }

    public UserInterface authenticate(Request request) {
        for (AuthHandler authenticationMethod : authenticationMethods) {
            final UserInterface login = authenticationMethod.login(request);

            if (login != null) {
                return login;
            }
        }
        return null;
    }


}
