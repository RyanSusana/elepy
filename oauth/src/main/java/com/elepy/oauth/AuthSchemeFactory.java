package com.elepy.oauth;

import com.elepy.oauth.github.GitHubEmailExtractor;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuthService;

public class AuthSchemeFactory<T extends OAuthService> {
    private String serviceName;
    private String icon;
    private String appSecret;

    private String scope;
//    private T service;
//    private EmailExtractor<T> emailExtractor;

    private ServiceBuilder serviceBuilder;
    private final String appId;
    private DefaultApi20 api20;
    private EmailExtractor emailExtractor;

    public AuthSchemeFactory(String appId) {
        this.serviceBuilder = new ServiceBuilder(appId);
        this.appId = appId;
    }


    public AuthScheme create(String callback) {
        return new AuthScheme(serviceName,
                icon,
                serviceBuilder
                        .defaultScope(scope)
                        .apiSecret(appSecret)
                        .callback(callback)
                        .build(api20),
                emailExtractor);
    }

}