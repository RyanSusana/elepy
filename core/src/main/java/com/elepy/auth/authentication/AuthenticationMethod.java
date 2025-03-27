package com.elepy.auth.authentication;

import com.elepy.http.Request;

import java.util.Optional;

public interface AuthenticationMethod {

    Optional<Credentials> getCredentials(Request request);

}
