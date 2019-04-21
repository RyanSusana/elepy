package com.elepy.routes;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.Filter;
import com.elepy.http.HttpContext;
import com.elepy.models.User;

import java.util.Collection;

public class UserPermissionFilter implements Filter {

    private final Collection<String> permissions;

    public UserPermissionFilter(Collection<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void authenticate(HttpContext context) {
        User user = context.attribute("user");

        if (!permissions.isEmpty() && user == null) {
            throw new ElepyException("You must be logged in to perform this procedure.", 401);
        }

        if (!permissions.isEmpty() && !user.getPermissions().containsAll(permissions)) {
            throw new ElepyException(String.format("%s is not authorized.", user.getUsername()), 401);
        }
    }
}
