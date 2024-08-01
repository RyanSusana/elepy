package com.elepy.auth.roles;

import com.elepy.auth.Policy;
import com.elepy.auth.Role;
import com.elepy.crud.Crud;
import com.elepy.handlers.SimpleCreate;
import com.elepy.http.Request;
import jakarta.inject.Inject;

public class RolesCreate extends SimpleCreate<Role> {
    @Inject
    private Policy policy;

    @Override
    public void beforeCreate(Role objectForCreation, Request httpRequest, Crud<Role> crud) throws Exception {
        policy.assureNotPredefinedRole(objectForCreation.getId());
    }

    @Override
    public void afterCreate(Role createdObject, Crud<Role> crud) {

    }
}
