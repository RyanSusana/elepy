package com.elepy.auth.roles;

import jakarta.inject.Inject;
import com.elepy.auth.Policy;
import com.elepy.auth.Role;
import com.elepy.dao.Crud;
import com.elepy.handlers.DefaultFindOne;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.models.ModelContext;

public class RolesFindOne extends DefaultFindOne<Role> {
    @Inject
    private Policy policy;

    @Override
    public Role findOne(Request request, Response response, Crud<Role> dao, ModelContext<Role> modelContext) {
        final var id = request.recordId().toString();
        if (policy.existsAsPredefinedRole(id)) {
            return policy.getRole(id).orElseThrow();
        }
        return super.findOne(request, response, dao, modelContext);
    }
}
