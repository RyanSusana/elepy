package com.elepy.auth.authorization;

import com.elepy.crud.Crud;
import com.elepy.query.Filters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URI;
import java.util.List;

@ApplicationScoped
public class DefaultPolicyLookup implements PolicyLookup{

    @Inject
    private Crud<PolicyBinding> policies;

    @Override
    public List<PolicyBinding> getPoliciesForTarget(URI target) {
        return policies.find(Filters.eq("target", target.toString()));
    }
}
