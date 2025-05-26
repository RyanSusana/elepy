package com.elepy.auth.authorization;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

import java.util.Map;
import java.util.Set;

@ApplicationScoped
@Default
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
