package com.elepy.auth;

import com.elepy.http.Request;

import java.util.Optional;

public abstract class TokenGenerator implements AuthenticationMethod {

    @Override
    public final Optional<Grant> getGrant(Request request) {
        final var token = request.token();

        if (token == null) {
            return Optional.empty();
        }

        final var user = validateToken(token);

        return Optional.ofNullable(user);
    }

    /**
     * Returning null or throwing an exception marks the token as invalid
     *
     * @param rawToken a non-null token that must be validated against.
     */
    public abstract Grant validateToken(String rawToken);


    /**
     * Translates a grant into a String. This grant can be stored in the return String via JWT, or in a database/in-memory.
     *
     * @param grant a grant. This grant assures that the user is who
     */
    public abstract String createToken(Grant grant);

}
