package com.elepy.auth;


import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;

import java.util.ArrayList;
import java.util.List;

public class UserAuthenticationCenter {

    private List<AuthenticationMethod> authenticationMethods = new ArrayList<>();


    public UserAuthenticationCenter addAuthenticationMethod(AuthenticationMethod authHandler) {
        authenticationMethods.add(authHandler);
        return this;
    }

    public void tryToLogin(Request request) {
        for (AuthenticationMethod authenticationMethod : authenticationMethods) {
            final User login = authenticationMethod.login(request);

            if (login != null) {
                request.attribute("user", login);
                return;
            }
        }
        throw new ElepyException("Failed to login.", 401);
    }


}
