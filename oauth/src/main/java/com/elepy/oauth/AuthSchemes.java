package com.elepy.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuthSchemes {
    private final List<AuthScheme> schemes = new ArrayList<>();

    public void addScheme(AuthScheme serviceWrapper) {
        schemes.add(serviceWrapper);
    }

    public AuthScheme getServiceWrapper(String s) {
        return schemes.stream().filter(oAuthServiceWrapper -> oAuthServiceWrapper.getServiceName().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public Collection<AuthScheme> getSchemes() {
        return new ArrayList<>(schemes);
    }
}
