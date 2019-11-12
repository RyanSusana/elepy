package com.elepy.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.elepy.auth.TokenAuthenticationMethod;
import com.elepy.auth.User;
import com.elepy.http.Request;

import java.util.Calendar;
import java.util.Optional;

public class JWTAuthenticationMethod implements TokenAuthenticationMethod {

    private static final int MAXIMUM_TOKEN_DURATION = 1000 * 60 * 60 ;

    private final Algorithm algorithm;

    public JWTAuthenticationMethod(Algorithm algorithm) {
        this.algorithm = algorithm;
    }


    @Override
    public Optional<User> authenticateUser( Request request) {

        final String token = request.cookie("ELEPY_TOKEN");

        if (token == null) {
            return Optional.empty();
        }

        try {
            final var decodedToken = JWT.require(algorithm).build().verify(token);

            final var userId = decodedToken.getClaim("userId").asString();
            final var username = decodedToken.getClaim("username").asString();
            final var permissions = decodedToken.getClaim("permissions").asList(String.class);

            return Optional.of(new User(userId, username, "", permissions));

        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    @Override
    public String createToken(User user, int duration) {

        final var expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.MILLISECOND, Math.min(duration, MAXIMUM_TOKEN_DURATION));

        return JWT.create()
                .withExpiresAt(expirationDate.getTime())
                .withClaim("userId", user.getId())
                .withClaim("username", user.getUsername())
                .withArrayClaim("permissions", user.getPermissions().toArray(new String[0]))
                .sign(algorithm);
    }

}
