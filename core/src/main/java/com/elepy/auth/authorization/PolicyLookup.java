package com.elepy.auth.authorization;

import java.net.URI;
import java.util.List;

public interface PolicyLookup {
    public List<PolicyBinding> getPoliciesForTarget(URI target);
}
