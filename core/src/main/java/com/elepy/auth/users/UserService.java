package com.elepy.auth.users;

import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authorization.AuthorizationService;
import com.elepy.auth.authorization.PolicyBinding;
import com.elepy.crud.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;
import com.elepy.id.HexIdentityProvider;
import com.elepy.utils.StringUtils;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.elepy.query.Filters.any;
import static com.elepy.query.Filters.eq;

@ApplicationScoped
public class UserService {

    @Inject
    private Crud<User> users;

    @Inject
    private Crud<PolicyBinding> policies;

    @Inject
    private AuthorizationService authorizationService;

    public void createUser(@Nullable Credentials credentials, User user) {
        if (users.count() > 0) {
            createAdditionalUser(credentials, user);
        } else {
            createInitialUser(user);
        }
    }

    public Optional<User> getUserFromCredentials(Credentials credentials) {
        return users.getById(credentials.getPrincipal());
    }

    public Credentials getCredentialsForUser(String userId) {
        final var user = users.getById(userId).orElseThrow();
        return getCredentialsForUser(user);
    }

    public Credentials getCredentialsForUser(User user) {
        final var grant = new Credentials();

        grant.setPrincipal(user.getId());
        grant.setDisplayName(user.getUsername());
        return grant;
    }

    public Crud<User> users() {
        return users;
    }


    public Optional<User> login(String usernameOrEmail, String password) {
        if (StringUtils.isEmpty(password)) return Optional.empty();

        Optional<User> user = getUserByUsername(usernameOrEmail);
        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }


    public boolean hasUsers() {
        return users.findOne(any()).isPresent();
    }

    public Optional<User> getUserByUsername(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            usernameOrEmail = usernameOrEmail.toLowerCase();
        }
        var all = users.getAll();
        return users.findOne(eq("username", usernameOrEmail));
    }

    private void createInitialUser(User user) {
        var before = users.getAll();
        storeUser(user);
        var policyBinding = new PolicyBinding();
        policyBinding.setId(UUID.randomUUID().toString());
        policyBinding.setPrincipal(user.getId());
        policyBinding.setRole("roles/admin");
        policyBinding.setTarget("/");

        policies.create(policyBinding);

        var policyBindings = policies.getAll();
        var all = users.getAll();

        if (all.size() != before.size() + 1) {
            throw new RuntimeException("Failed to create initial user");
        }
    }

    private void createAdditionalUser(Credentials credentials, User user) {
        if (credentials == null) throw ElepyException.notAuthorized();

        authorizationService.ensurePrincipalHasPermissions(
                credentials.getPrincipal(),
                URI.create("/users"),
                "users/create");

        evaluateIntegrity(user);
        storeUser(user);
    }

    private void evaluateIntegrity(User user) {
        new DefaultIntegrityEvaluator<>(users).evaluate(user, EvaluationType.CREATE);
    }

    private void storeUser(User user) {
        user.cleanUsername();
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        //This line didn't exist before for some reason it got deleted
        new HexIdentityProvider().provideId(user, users);

        users.create(user);

    }
}
