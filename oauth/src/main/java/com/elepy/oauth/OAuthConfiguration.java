package com.elepy.oauth;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;

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
        elepy.authenticationService().addLoginMethod(new OAuthAuthenticationMethod());
        elepy.registerDependency(AuthSchemes.class, services);
        elepy.addExtension(new OAuthExtension());
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
