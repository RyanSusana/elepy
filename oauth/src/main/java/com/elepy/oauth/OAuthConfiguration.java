package com.elepy.oauth;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;

public class OAuthConfiguration implements Configuration {
    private final AuthSchemes services;

    public OAuthConfiguration(AuthSchemes services) {
        this.services = services;
    }

    public static OAuthConfiguration of(AuthScheme... schemes) {
        final var authSchemes = new AuthSchemes();

        for (AuthScheme scheme : schemes) {
            authSchemes.addScheme(scheme);
        }
        return new OAuthConfiguration(authSchemes);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.authenticationService().addAuthenticationMethod(new OAuthAuthenticationMethod());
        elepy.registerDependency(AuthSchemes.class, services);
        elepy.addExtension(new OAuthExtension());
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
