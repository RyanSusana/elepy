package com.elepy.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Permissions {
    public static final String SUPER_USER = "owner";
    public static final String CAN_ADMINISTRATE_FILES = "files";
    public static final String CAN_ADMINISTRATE_USERS = "users";
    public static final String AUTHENTICATED = "authenticated";
    public static final String[] NONE = new String[]{};
    public static final String[] DEFAULT = new String[]{AUTHENTICATED};


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
        return (grantedPermissions.stream().map(String::toLowerCase).collect(Collectors.toSet())
                .containsAll(permissionsToCheck.stream().map(String::toLowerCase).collect(Collectors.toSet())));
    }


} 
