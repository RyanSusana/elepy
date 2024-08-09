package com.elepy.auth.roles;

import com.elepy.crud.Crud;
import com.elepy.handlers.DefaultFindOne;
import com.elepy.http.Request;
import com.elepy.http.Response;
import com.elepy.igniters.ModelContext;
import jakarta.inject.Inject;

public class RolesFindOne extends DefaultFindOne<Role> {
    @Inject
    private RolesService policy;

    @Override
    public Role findOne(Request request, Response response, Crud<Role> dao, ModelContext<Role> modelContext) {
        final var id = request.recordId().toString();
        if (policy.existsAsPredefinedRole(id)) {
            return policy.getRole(id).orElseThrow();
        }
        return super.findOne(request, response, dao, modelContext);
    }
}
