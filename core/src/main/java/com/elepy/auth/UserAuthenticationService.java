package com.elepy.auth;


import com.elepy.http.Request;

import java.util.ArrayList;
import java.util.List;

public class UserAuthenticationService {

    private List<AuthenticationMethod> authenticationMethods = new ArrayList<>();


    public UserAuthenticationService addAuthenticationMethod(AuthenticationMethod authHandler) {
        authenticationMethods.add(authHandler);
        return this;
    }

    public void tryToLogin(Request request) {
        if (request.attribute("user") == null) {
            for (AuthenticationMethod authenticationMethod : authenticationMethods) {
                final User login = authenticationMethod.getUserFromRequest(request);

                if (login != null) {
                    request.attribute("user", login);
                }
            }
        }
    }


}
