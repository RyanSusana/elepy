package com.elepy.auth;

import com.elepy.http.Request;

import java.util.Optional;

public interface AuthenticationMethod {

    Optional<Grant> getGrant(Request request);

}
