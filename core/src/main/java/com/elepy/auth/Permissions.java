package com.elepy.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Permissions {
    public static final String SUPER_USER = "SUPER_USER";
    public static final String CAN_ADMINISTRATE_USERS = "CAN_ADMIN_USERS";
    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String[] PUBLIC = {};

    private final Set<String> grantedPermissions = new TreeSet<>();


    public void addPermissions(String... permissions) {
        addPermissions(Arrays.asList(permissions));
    }

    public void addPermissions(Collection<String> permissions) {
        this.grantedPermissions.addAll(permissions);
    }

    public boolean hasPermissions(Collection<String> permissionsToCheck) {
        if (grantedPermissions.contains(SUPER_USER) || permissionsToCheck.isEmpty()) {
            return true;
        }
        return (grantedPermissions.containsAll(permissionsToCheck));
    }


} 
