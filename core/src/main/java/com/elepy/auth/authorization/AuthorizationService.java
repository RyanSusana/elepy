package com.elepy.auth.authorization;

import com.elepy.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuthorizationService {

    private static Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Inject
    private PolicyLookup policies;

    @Inject
    private RoleLookup roles;

    public void setPolicies(PolicyLookup policies) {
        this.policies = policies;
    }

    public void setRoles(RoleLookup roles) {
        this.roles = roles;
    }

    public AuthorizationResult testPermissions(String principalToCheck, URI mainTarget, String... permissions) {
        return testPermissions(principalToCheck, mainTarget, Set.of(permissions));
    }

    public AuthorizationResult testPermissions(String principalToCheck, URI mainTarget, Set<String> permissions) {
        var permissionsSet = normalizePermissions(permissions);
        Set<String> permissionsRemainingToGrant = new TreeSet<>(permissionsSet);

        // You only need to lookup roles once per request, so we cache to avoid unnecessary lookups
        var cachedRoles = new CachedRoleLookup(roles);
        var searchList = getTargetSearchList(mainTarget);
        logger.debug("Checking policies in this target resource order: {}", searchList.stream().map(URI::toString).collect(Collectors.joining(" -> ")));

        for (var currentTarget : searchList) {
            if (permissionsRemainingToGrant.isEmpty()) break;

            // Consider optimizing this to do a full lookup once
            var currentPolicies = policies.getPoliciesForTarget(currentTarget);

            for (var policy : currentPolicies) {
                // If the principal does not match then we don't need to check
                if (!principalMatch(principalToCheck, policy.getPrincipal())) continue;

                var role = policy.getRole();
                Set<String> grantedPermissions = cachedRoles.getPermissionsForRole(role);

                permissionsRemainingToGrant = removeGrantedPermissions(permissionsRemainingToGrant, grantedPermissions);
            }
        }

        var authorizationResult = new AuthorizationResult(principalToCheck, mainTarget, permissionsSet, permissionsRemainingToGrant);

        logger.debug("Authorization on {} for {} is {}.", authorizationResult.getTarget(), authorizationResult.getPrincipal(), authorizationResult.isSuccessful() ? "successful" : "unsuccessful");
        return authorizationResult;
    }


    private Set<String> normalizePermissions(Set<String> original) {
        return original.stream().filter(
                permission -> !StringUtils.isEmpty(permission)
        ).collect(Collectors.toSet());
    }

    private Set<String> removeGrantedPermissions(Set<String> permissionsNeeded, Set<String> permissionsGranted) {
        var permissionsLeftToGrant = new TreeSet<>(permissionsNeeded);

        for (var permissionGranted : permissionsGranted) {
            permissionsLeftToGrant.removeIf(permissionToCheck ->
                    permissionMatch(permissionToCheck, permissionGranted));
        }

        return permissionsLeftToGrant;
    }

    private List<URI> getTargetSearchList(final URI target) {
        var curTarget = target;
        var targets = new ArrayList<URI>();
        while (!isRootTarget(curTarget)) {
            targets.add(curTarget);
            curTarget = getParentTarget(curTarget);
        }
        // Always add root
        targets.add(URI.create("/"));

        return targets.reversed();

    }

    private boolean isRootTarget(URI uri) {
        return uri == null || uri.getPath().isEmpty() || uri.getPath().equals("/");
    }

    /**
     * Checks whether two principals are equivalent
     * FUTURE: Support for Groups ;)
     */
    private boolean principalMatch(String principalToCheck, String principal) {
        return Objects.equals(principal, principalToCheck);
    }

    /**
     * A target is a URI
     */
    private URI getParentTarget(URI uri) {
        return uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");

    }

    /**
     * Checks if the permission on the left is a superset of the right, or if they are equal
     * Examples
     * permissionsMatch(resource.permissions.read, resource.permissions.read) == true
     * permissionsMatch(resource.permissions.read, resource.permissions.write) == false
     * permissionsMatch(resource.permissions.*, resource.permissions.write) == true
     * permissionsMatch(resource.*, resources.permissions.write) == true
     * permissionsMatch(*, anything) == true
     */
    private static boolean permissionMatch(String permission1, String permission2) {
        return permission1.matches(permission2.replace("?", ".?").replace("*", ".*?"));
    }
}
