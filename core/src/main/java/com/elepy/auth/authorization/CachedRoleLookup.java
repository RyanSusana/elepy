package com.elepy.auth.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CachedRoleLookup implements RoleLookup {
    private final RoleLookup roleLookup;
    private final Map<String, Set<String>> cache = new HashMap<>();

    public CachedRoleLookup(RoleLookup roleLookup) {
        this.roleLookup = roleLookup;
    }

    public Set<String> getPermissionsForRole(String role) {
        Set<String> permissions = cache.get(role);
        if (permissions == null || permissions.isEmpty()) {
            permissions = roleLookup.getPermissionsForRole(role);
            cache.put(role, permissions);
        }
        return permissions;
    }
}
