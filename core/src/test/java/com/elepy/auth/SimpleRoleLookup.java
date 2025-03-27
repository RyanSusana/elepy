package com.elepy.auth;

import com.elepy.auth.authorization.RoleLookup;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SimpleRoleLookup implements RoleLookup {
    private Map<String, Set<String>> roles = new HashMap<>();
    @Override
    public Set<String> getPermissionsForRole(String role) {
        return Optional.ofNullable(this.roles.get(role)).orElse(Set.of());

    }

    public Map<String, Set<String>> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Set<String>> roles) {
        this.roles = roles;
    }
}
