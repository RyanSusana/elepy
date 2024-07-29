package com.elepy.auth.roles;

import com.elepy.auth.Policy;
import com.elepy.auth.Role;
import com.elepy.handlers.DefaultUpdate;
import com.elepy.handlers.HandlerContext;
import jakarta.inject.Inject;

import java.util.Objects;

public class RolesUpdate extends DefaultUpdate<Role> {
    @Inject
    private Policy policy;

    @Override
    public void handle(HandlerContext<Role> ctx) throws Exception {

        final var context = ctx.http();
        context.recordIds().stream().map(Objects::toString).forEach(policy::assureNotPredefinedRole);
        super.handle(ctx);
    }
}
