package com.elepy.oauth.microsoft;

import com.github.scribejava.apis.MicrosoftAzureActiveDirectory20Api;
import com.github.scribejava.apis.openid.OpenIdJsonTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

public class MicrosoftApi extends MicrosoftAzureActiveDirectory20Api {
    public MicrosoftApi(String tenant) {
        super(tenant == null ? "common" : tenant);
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OpenIdJsonTokenExtractor.instance();
    }
}
