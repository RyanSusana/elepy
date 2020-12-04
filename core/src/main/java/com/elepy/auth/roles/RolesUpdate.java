package com.elepy.auth.roles;

import com.elepy.annotations.Inject;
import com.elepy.auth.Policy;
import com.elepy.auth.Role;
import com.elepy.handlers.Context;
import com.elepy.handlers.DefaultUpdate;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import java.util.Objects;

public class RolesUpdate extends DefaultUpdate<Role> {
    @Inject
    private Policy policy;

    @Override
    public void handle(Context<Role> ctx) throws Exception {

        final var context = ctx.http();
        context.recordIds().stream().map(Objects::toString).forEach(policy::assureNotPredefinedRole);
        super.handle(ctx);
    }
}
