package com.elepy.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.exceptions.ElepyConfigException;

import java.util.Optional;

public class JWTConfiguration implements Configuration {

    private final Algorithm algorithm;

    private JWTConfiguration(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public static JWTConfiguration of(Algorithm algorithm) {
        return new JWTConfiguration(algorithm);
    }

    public static JWTConfiguration HMAC256(String secret) {
        return of(Algorithm.HMAC256(secret));
    }

    public static JWTConfiguration fromEnv() {
        return of(null);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        if (algorithm != null) {
            elepy.addAuthenticationMethod(new JWTAuthenticationMethod(algorithm));
        } else {
            final var secret = Optional.ofNullable(elepy.getPropertyConfig().getString("jwt_secret"))
                    .orElseThrow(() -> new ElepyConfigException("No jwt_secret found in Elepy properties or environmental variables"));

            elepy.addAuthenticationMethod(new JWTAuthenticationMethod(Algorithm.HMAC256(secret)));
        }

    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
