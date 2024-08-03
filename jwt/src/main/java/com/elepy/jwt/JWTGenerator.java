package com.elepy.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.elepy.auth.AuthenticatedCredentials;
import com.elepy.auth.methods.tokens.TokenAuthority;

import java.util.Calendar;

public class JWTGenerator extends TokenAuthority {

    private static final int MAXIMUM_TOKEN_DURATION = 1000 * 60 * 60;

    private final Algorithm algorithm;

    public JWTGenerator(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public AuthenticatedCredentials validateToken(String rawToken) {
        try {
            final var decodedToken = JWT.require(algorithm).build().verify(rawToken);

            final var userId = decodedToken.getClaim("userId").asString();
            final var username = decodedToken.getClaim("username").asString();
            final var permissions = decodedToken.getClaim("permissions").asList(String.class);

            final var grant = new AuthenticatedCredentials();

            grant.setPermissions(permissions);
            grant.setUserId(userId);
            grant.setUsername(username);
            return grant;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    @Override
    public String createToken(AuthenticatedCredentials authenticatedCredentials) {

        final var expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.MILLISECOND, MAXIMUM_TOKEN_DURATION);

        return JWT.create()
                .withExpiresAt(expirationDate.getTime())
                .withClaim("userId", authenticatedCredentials.getUserId())
                .withClaim("username", authenticatedCredentials.getUsername())
                .withArrayClaim("permissions", authenticatedCredentials.getPermissions().toArray(new String[0]))
                .sign(algorithm);
    }
}
