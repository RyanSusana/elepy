package com.elepy.auth;

import com.elepy.http.Request;

import java.util.Optional;

public interface AuthenticationMethod {

    Optional<User> authenticateUser(Request request);

}
