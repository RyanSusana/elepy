package com.elepy.auth.authentication.methods.tokens;

import com.elepy.auth.authentication.AuthenticationMethod;
import com.elepy.auth.authentication.Credentials;
import com.elepy.http.Request;

import java.util.Optional;

public abstract class TokenAuthority implements AuthenticationMethod {

    @Override
    public final Optional<Credentials> getCredentials(Request request) {
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
    public abstract Credentials validateToken(String rawToken);


    /**
     * Translates credentials into a String. These credentials can be stored in the return String via JWT, or in a database/in-memory.
     *
     * @param credentials a grant. This grant assures that the user is who
     */
    public abstract String createToken(Credentials credentials);

}
