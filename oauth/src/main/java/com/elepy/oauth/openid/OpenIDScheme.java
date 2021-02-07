package com.elepy.oauth.openid;

import com.elepy.oauth.AuthScheme;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

public class OpenIDScheme extends AuthScheme {


    public OpenIDScheme(String serviceName, String icon, String wellKnownEndpoint, String clientId, String clientSecret) {
        super(serviceName,
                icon, new ServiceBuilder(clientId).defaultScope("openid email")
                        .apiSecret(clientSecret)
                        .build(new OpenIDApi(wellKnownEndpoint)), new OpenIDEmailExtractor());
    }
}
