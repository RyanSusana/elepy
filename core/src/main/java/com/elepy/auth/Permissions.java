package com.elepy.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Permissions {
    public static final String SUPER_USER = "owner";
    public static final String CAN_ADMINISTRATE_FILES = "files";
    public static final String CAN_ADMINISTRATE_USERS = "users";
    public static final String AUTHENTICATED = "authenticated";
    public static final String[] NONE = new String[]{};
    public static final String[] DEFAULT = new String[]{AUTHENTICATED};


    private final Set<String> grantedPermissions = new TreeSet<>();


    public void grantPermission(String... permissions) {
        grantPermission(Arrays.asList(permissions));
    }

    public void grantPermission(Collection<String> permissions) {
        this.grantedPermissions.addAll(permissions);
    }

    public boolean hasPermissions(Collection<String> permissionsToCheck) {
        if (grantedPermissions.contains(SUPER_USER) || permissionsToCheck.isEmpty()) {
            return true;
        }

        return permissionsToCheck.stream().allMatch(this::hasPermission);
    }

    public boolean hasPermission(String permission) {
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
