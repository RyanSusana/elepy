package com.elepy.auth;

import com.elepy.http.Filter;
import com.elepy.http.HttpContext;

import java.util.Collection;

public class UserPermissionFilter implements Filter {

    private final Collection<String> requiredPermissions;

    public UserPermissionFilter(Collection<String> permissions) {
        this.requiredPermissions = permissions;
    }

    @Override
    public void authenticate(HttpContext context) {
        context.requirePermissions(requiredPermissions);
    }
}
