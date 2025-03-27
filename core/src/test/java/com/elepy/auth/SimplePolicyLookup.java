package com.elepy.auth;

import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.auth.authorization.PolicyLookup;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimplePolicyLookup implements PolicyLookup {
    public Map<String, List<PolicyBinding>> getPolicyBindings() {
        return policyBindings;
    }

    public void setPolicyBindings(Map<String, List<PolicyBinding>> policyBindings) {
        this.policyBindings = policyBindings;
    }

    private Map<String, List<PolicyBinding>> policyBindings = new HashMap<>();

    @Override
    public List<PolicyBinding> getPoliciesForTarget(URI target) {
        return Optional.ofNullable(policyBindings.get(target.toString())).orElse(List.of());
    }
}
