package com.elepy.auth.roles;

import com.elepy.crud.Crud;
import com.elepy.handlers.DefaultFindMany;
import com.elepy.http.HttpContext;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class RolesFind extends DefaultFindMany<Role> {

    @Inject
    private RolesService policy;

    @Override
    public List<Role> find(HttpContext context, Crud<Role> dao) {
        final var roles = new ArrayList<>(policy.getPredefinedRoles());

        if (context.queryParams("ids") == null)
            roles.addAll(super.find(context, dao));
        return roles;
    }

    @Override
    public long count(HttpContext context, Crud<Role> dao) {

        return super.count(context, dao) + (context.queryParams("ids") == null ? policy.getPredefinedRoles().size() : 0);
    }
}
