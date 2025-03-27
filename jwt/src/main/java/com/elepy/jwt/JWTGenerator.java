package com.elepy.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authentication.methods.tokens.TokenAuthority;

import java.util.Calendar;

public class JWTGenerator extends TokenAuthority {

    private static final int MAXIMUM_TOKEN_DURATION = 1000 * 60 * 60;

    private final Algorithm algorithm;

    public JWTGenerator(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public Credentials validateToken(String rawToken) {
        try {
            final var decodedToken = JWT.require(algorithm).build().verify(rawToken);

            final var userId = decodedToken.getClaim("sub").asString();
            final var username = decodedToken.getClaim("displayName").asString();

            final var grant = new Credentials();

            grant.setPrincipal(userId);
            grant.setDisplayName(username);
            return grant;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    @Override
    public String createToken(Credentials credentials) {

        final var expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.MILLISECOND, MAXIMUM_TOKEN_DURATION);

        return JWT.create()
                .withExpiresAt(expirationDate.getTime())
                .withClaim("sub", credentials.getPrincipal())
                .withClaim("displayName", credentials.getDisplayName())
                .sign(algorithm);
    }
}
