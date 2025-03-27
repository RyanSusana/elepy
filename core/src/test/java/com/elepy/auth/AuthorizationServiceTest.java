package com.elepy.auth;

import com.elepy.auth.authorization.AuthorizationService;
import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.auth.authorization.PolicyLookup;
import com.elepy.auth.authorization.RoleLookup;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthorizationServiceTest {

    void test_SuccessfulWith_NullOrBlankPermissions() {

    }

    @Test
    void test_Successful_DirectPolicyBinding() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of(
                        "Admin", Set.of("resources.*")
                )
        );
        policies.setPolicyBindings(Map.of(
                "/products", List.of(policy("ryan", "Admin"))
        ));

        var authorization = authorizationService(policies, roles);

        assertThat(authorization.testPermissions(
                "ryan",
                URI.create("/products"),
                "resources.get").isSuccessful()).isTrue();
    }

    @Test
    void test_Successful_IndirectPolicyBinding_OneLevelHigher() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of(
                        "Viewer", Set.of("resources.get", "resources.list")
                )
        );
        policies.setPolicyBindings(Map.of(
                "/", List.of(policy("george", "Viewer"))
        ));

        var authorization = authorizationService(policies, roles);

        assertThat(authorization.testPermissions(
                "george",
                URI.create("/products"),
                "resources.get").isSuccessful()).isTrue();
    }

    @Test
    void test_Successful_PolicyWithWildcard() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of("Admin", Set.of("resources.*"))
        );
        policies.setPolicyBindings(Map.of(
                "/products", List.of(policy("ryan", "Admin"))
        ));

        var authorization = authorizationService(policies, roles);


        assertThat(authorization.testPermissions(
                "ryan",
                URI.create("/products"),
                "resources.canDoAnything").isSuccessful()).isTrue();
    }

    @Test
    void test_Failed_UnknownPrincipal() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of(
                        "Viewer", Set.of("resources.get", "resources.list"),
                        "Editor", Set.of("resources.get", "resources.list", "resources.create", "resources.update", "resources.delete"),
                        "Admin", Set.of("resources.*")
                )
        );
        policies.setPolicyBindings(Map.of(
                "/", List.of(policy("ryan", "Viewer"), policy("george", "Viewer")),
                "/users", List.of(policy("george", "Writer")),
                "/products", List.of(policy("ryan", "Admin"))
        ));

        var authorization = authorizationService(policies, roles);

        assertThat(authorization.testPermissions(
                "notfound",
                URI.create("/products"),
                "resources.delete").isSuccessful()).isFalse();
    }


    @Test
    void test_Failed_NullPrincipal() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of(
                        "Viewer", Set.of("resources.get", "resources.list"),
                        "Editor", Set.of("resources.get", "resources.list", "resources.create", "resources.update", "resources.delete"),
                        "Admin", Set.of("resources.*")
                )
        );
        policies.setPolicyBindings(Map.of(
                "/", List.of(policy("ryan", "Viewer"), policy("george", "Viewer")),
                "/users", List.of(policy("george", "Writer")),
                "/products", List.of(policy("ryan", "Admin"))
        ));

        var authorization = authorizationService(policies, roles);

        assertThat(authorization.testPermissions(
                null,
                URI.create("/products"),
                "resources.delete").isSuccessful()).isFalse();
    }

    @Test
    void test_Failed_WildcardPermission() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of(
                        "Viewer", Set.of("resources.get", "resources.list"),
                        "Editor", Set.of("resources.get", "resources.list", "resources.create", "resources.update", "resources.delete"),
                        "Admin", Set.of("resources.*")
                )
        );
        policies.setPolicyBindings(Map.of(
                "/", List.of(policy("ryan", "Viewer"), policy("george", "Viewer")),
                "/users", List.of(policy("george", "Writer")),
                "/products", List.of(policy("ryan", "Admin"))
        ));

        var authorization = authorizationService(policies, roles);

        assertThat(authorization.testPermissions(
                "george",
                URI.create("/users"),
                "*").isSuccessful()).isFalse();
    }


    @Test
    void testSimplePolicy() {
        var policies = new SimplePolicyLookup();
        var roles = new SimpleRoleLookup();

        roles.setRoles(
                Map.of(
                        "Viewer", Set.of("resources.get", "resources.list"),
                        "Editor", Set.of("resources.get", "resources.list", "resources.create", "resources.update", "resources.delete"),
                        "Admin", Set.of("resources.*")
                )
        );
        policies.setPolicyBindings(Map.of(
                "/", List.of(policy("ryan", "Viewer"), policy("george", "Viewer")),
                "/users", List.of(policy("george", "Writer")),
                "/products", List.of(policy("ryan", "Admin"))
        ));

        var authorization = authorizationService(policies, roles);


        assertThat(authorization.testPermissions(
                "george",
                URI.create("/products"),
                "resources.delete").isSuccessful())
                .isFalse();

        assertThat(authorization.testPermissions(
                "notfound",
                URI.create("/products"),
                "resources.delete").isSuccessful()).isFalse();

        assertThat(authorization.testPermissions(
                "notfound",
                URI.create("/products"),
                "resources.delete").isSuccessful()).isFalse();

    }

    public PolicyBinding policy(String principal, String role) {
        var policyBinding = new PolicyBinding();

        policyBinding.setPrincipal(principal);
        policyBinding.setRole(role);
        return policyBinding;
    }

    public AuthorizationService authorizationService(PolicyLookup policyLookup, RoleLookup roleLookup) {
        var authorizationService = new AuthorizationService();

        authorizationService.setPolicies(policyLookup);
        authorizationService.setRoles(roleLookup);

        return authorizationService;
    }
}
