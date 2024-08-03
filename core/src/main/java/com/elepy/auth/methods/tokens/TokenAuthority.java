package com.elepy.auth.methods.tokens;

import com.elepy.auth.AuthenticationMethod;
import com.elepy.auth.AuthenticatedCredentials;
import com.elepy.http.Request;

import java.util.Optional;

public abstract class TokenAuthority implements AuthenticationMethod {

    @Override
    public final Optional<AuthenticatedCredentials> getGrant(Request request) {
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
    public abstract AuthenticatedCredentials validateToken(String rawToken);


    /**
     * Translates a grant into a String. This grant can be stored in the return String via JWT, or in a database/in-memory.
     *
     * @param authenticatedCredentials a grant. This grant assures that the user is who
     */
    public abstract String createToken(AuthenticatedCredentials authenticatedCredentials);

}
