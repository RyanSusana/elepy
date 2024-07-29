package com.elepy.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.scribejava.core.oauth.OAuth20Service;

public class AuthScheme {

    @JsonProperty("scheme")
    private final String serviceName;

    private final String icon;

    @JsonIgnore
    private final OAuth20Service service;

    @JsonIgnore
    private final EmailExtractor emailExtractor;

    public AuthScheme(String serviceName, String icon, OAuth20Service service, EmailExtractor emailExtractor) {
        this.serviceName = serviceName;
        this.icon = icon;
        this.service = service;
        this.emailExtractor = emailExtractor;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getAuthorizationUrl(String callback, String state) {


        return service.getApi().getAuthorizationUrl(getService().getResponseType(), service.getApiKey(), callback, service.getDefaultScope(), state, null);
    }

    public OAuth20Service getService() {

        return service;
    }


    public String getIcon() {
        return icon;
    }

    public EmailExtractor getEmailExtractor() {
        return emailExtractor;
    }
}
