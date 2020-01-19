package com.elepy.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Property;
import com.elepy.exceptions.ElepyConfigException;

import java.util.Optional;

public class JWTConfiguration implements Configuration {

    private final Algorithm algorithm;

    @ElepyConstructor
    public JWTConfiguration(
            @Property(key = "${jwt.secret}") String secret
    ) {
        this(Algorithm.HMAC256(secret));
    }

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
            final var secret = Optional.ofNullable(elepy.getPropertyConfig().getString("jwt.secret"))
                    .orElseThrow(() -> new ElepyConfigException("No jwt.secret found in Elepy properties or environmental variables"));

            elepy.addAuthenticationMethod(new JWTAuthenticationMethod(Algorithm.HMAC256(secret)));
        }

    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
