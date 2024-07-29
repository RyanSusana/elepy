package com.elepy.auth.permissions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static com.elepy.auth.permissions.DefaultPermissions.DISABLED;
import static com.elepy.auth.permissions.DefaultPermissions.SUPER_USER;

public class Permissions {
    private final Set<String> grantedPermissions = new TreeSet<>();

    public void grantPermission(String... permissions) {
        grantPermission(Arrays.asList(permissions));
    }

    public void grantPermission(Collection<String> permissions) {
        this.grantedPermissions.addAll(permissions);
    }

    public boolean hasPermissions(Collection<String> permissionsToCheck) {
        if (permissionsToCheck.contains(DISABLED)) {
            return false;
        }
        if (grantedPermissions.contains(SUPER_USER) || permissionsToCheck.isEmpty()) {
            return true;
        }

        return permissionsToCheck.stream().allMatch(this::hasPermission);
    }

    private boolean hasPermission(String permission) {
        return grantedPermissions.stream()
                .anyMatch(grantedPermission -> permissionMatch(permission.toLowerCase(), grantedPermission.toLowerCase()));
    }

    private static boolean permissionMatch(String permission1, String permission2) {

        if (SUPER_USER.equals(permission1) || SUPER_USER.equals(permission2)) {
            return SUPER_USER.equals(permission1) && SUPER_USER.equals(permission2);
        }
        return permission1.matches(permission2.replace("?", ".?").replace("*", ".*?"));
    }

} 
