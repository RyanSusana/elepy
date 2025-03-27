package com.elepy.auth.authorization;

import java.util.Map;
import java.util.Set;

public class DefaultRoleLookup implements RoleLookup{

    private static Map<String, Set<String>> BUILT_IN_ROLES = Map.of(
            "roles/admin", Set.of("*"),
            "roles/resourceAdmin", Set.of("resources.*"),
            "roles/resourceReader", Set.of("resources.find")

    );
    public DefaultRoleLookup() {
    }

    @Override
    public Set<String> getPermissionsForRole(String role) {
        return BUILT_IN_ROLES.getOrDefault(role, Set.of());
    }
}
