package com.elepy.oauth;

import com.elepy.ElepyExtension;
import com.elepy.ElepyPostConfiguration;
import com.elepy.http.HttpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.oauth.OAuth20Service;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public class OAuthExtension implements ElepyExtension {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private AuthSchemes services;

    @Override
    public void setup(HttpService http, ElepyPostConfiguration elepy) {

        http.get("/elepy/auth-schemes", ctx -> ctx.response().json(services.getSchemes()));
        http.get("/elepy/auth-url", ctx -> {
            final var serviceWrapper = services.getServiceWrapper(ctx.queryParams("scheme"));
            final var service = (OAuth20Service) serviceWrapper.getService();
            Map<String, String> state = new HashMap<>();
            final var callback = ctx.queryParamOrDefault("redirect_uri", ctx.scheme() + "://" + ctx.host() + "/elepy/admin/login");
            state.put("scheme", serviceWrapper.getServiceName());
            state.put("redirect_uri", callback);


            final String authorizationUrl = serviceWrapper.getAuthorizationUrl(callback, objectMapper.writeValueAsString(state));
            ctx.redirect(authorizationUrl, 303);
        });

    }

}
