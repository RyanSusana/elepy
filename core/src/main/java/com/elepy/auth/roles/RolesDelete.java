package com.elepy.auth.roles;

import com.elepy.annotations.Inject;
import com.elepy.auth.Role;
import com.elepy.auth.Policy;
import com.elepy.handlers.DefaultDelete;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;

import java.util.Objects;

public class RolesDelete extends DefaultDelete<Role> {

    @Inject
    private Policy policy;

    @Override
    public void handle(HttpContext context, ModelContext<Role> modelContext) throws Exception {
        context.recordIds().stream().map(Objects::toString).forEach(policy::assureNotPredefinedRole);
        super.handle(context, modelContext);
    }
}
