package com.elepy.auth;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.Filter;
import com.elepy.http.HttpContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UserPermissionFilter implements Filter {

    private final Collection<String> requiredPermissions;

    public UserPermissionFilter(Collection<String> permissions) {
        this.requiredPermissions = permissions;
    }

    @Override
    public void authenticate(HttpContext context) {

        if (requiredPermissions.isEmpty()) {
            return;
        }
        Set<String> loggedInPermissions = new TreeSet<>();
        User user = context.attribute("user");
        if (user != null) {
            if (user.getPermissions().contains("SUPER_USER")) {
                return;
            }
            loggedInPermissions.addAll(user.getPermissions());
        }
        List<String> morePermissions = context.attribute("permissions");

        loggedInPermissions.addAll(morePermissions);

        if (!requiredPermissions.isEmpty() && !loggedInPermissions.containsAll(requiredPermissions)) {
            throw new ElepyException("User is not authorized.", 401);
        }
    }
}
