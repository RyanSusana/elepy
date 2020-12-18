package com.elepy.auth.roles;

import com.elepy.annotations.Inject;
import com.elepy.auth.Role;
import com.elepy.auth.Policy;
import com.elepy.handlers.HandlerContext;
import com.elepy.handlers.DefaultDelete;

import java.util.Objects;

public class RolesDelete extends DefaultDelete<Role> {

    @Inject
    private Policy policy;

    @Override
    public void handle(HandlerContext<Role> ctx) throws Exception {
        ctx.http().recordIds().stream().map(Objects::toString).forEach(policy::assureNotPredefinedRole);
        super.handle(ctx);
    }
}
