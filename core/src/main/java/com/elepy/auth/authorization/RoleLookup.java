package com.elepy.auth.authorization;

import java.util.Set;

public interface RoleLookup {
    public Set<String> getPermissionsForRole(String role);
}
