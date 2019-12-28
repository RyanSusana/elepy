package com.elepy.auth;

import com.elepy.http.Request;

import java.util.Optional;

public interface AuthenticationMethod {

    Optional<? extends User> authenticateUser(Request request);

}
