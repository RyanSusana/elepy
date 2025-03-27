package com.elepy.auth.authorization;

import java.net.URI;
import java.util.Set;

public class AuthorizationResult {
    private final String principal;
    private final URI target;
    private final Set<String> originalPermissions;
    private final Set<String> missingPermissions;

    public AuthorizationResult(String principal, URI target, Set<String> originalPermissions, Set<String> missingPermissions) {
        this.principal = principal;
        this.target = target;
        this.originalPermissions = originalPermissions;
        this.missingPermissions = missingPermissions;
    }

    public String getPrincipal() {
        return principal;
    }

    public URI getTarget() {
        return target;
    }

    public Set<String> getOriginalPermissions() {
        return originalPermissions;
    }

    public Set<String> getMissingPermissions() {
        return this.originalPermissions;
    }

    public boolean isSuccessful() {
        return this.missingPermissions == null || this.missingPermissions.isEmpty();
    }
}
