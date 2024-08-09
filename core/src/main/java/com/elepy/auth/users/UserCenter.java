package com.elepy.auth.users;

import com.elepy.auth.AuthenticatedCredentials;
import com.elepy.auth.roles.RolesService;
import com.elepy.crud.Crud;
import com.elepy.utils.StringUtils;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static com.elepy.query.Filters.any;
import static com.elepy.query.Filters.eq;

public class UserCenter {

    @Inject
    private Crud<User> users;

    @Inject
    private RolesService policy;

    public Optional<User> getUserFromGrant(AuthenticatedCredentials authenticatedCredentials) {
        return users.getById(authenticatedCredentials.getUserId());
    }

    public AuthenticatedCredentials getGrantForUser(String userId) {
        final var user = users.getById(userId).orElseThrow();
        return getGrantForUser(user);
    }

    public AuthenticatedCredentials getGrantForUser(User user) {

        final var grant = new AuthenticatedCredentials();

        grant.setUserId(user.getId());
        grant.setPermissions(policy.getPermissionsForUser(user));
        grant.setUsername(user.getUsername());
        return grant;
    }

    public Crud<User> users() {
        return users;
    }


    public Optional<User> login(String usernameOrEmail, String password) {
        Optional<User> user = getUserByUsername(usernameOrEmail);

        if (StringUtils.isEmpty(password)) {
            return Optional.empty();
        }

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
        return users.findOne(eq("username", usernameOrEmail));
    }


}
