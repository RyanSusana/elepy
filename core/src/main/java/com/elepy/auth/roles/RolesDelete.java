package com.elepy.auth.roles;

import com.elepy.auth.RolesService;
import com.elepy.auth.Role;
import com.elepy.handlers.DefaultDelete;
import com.elepy.handlers.HandlerContext;
import jakarta.inject.Inject;

import java.util.Objects;

public class RolesDelete extends DefaultDelete<Role> {

    @Inject
    private RolesService policy;

    @Override
    public void handle(HandlerContext<Role> ctx) throws Exception {
        ctx.http().recordIds().stream().map(Objects::toString).forEach(policy::assureNotPredefinedRole);
        super.handle(ctx);
    }
}
