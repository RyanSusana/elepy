package com.elepy.auth;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static com.elepy.dao.Filters.eq;

public class UserCenter {

    @Inject
    private Crud<User> users;

    @Inject
    private Policy policy;

    public Optional<User> getUserFromGrant(Grant grant) {
        return users.getById(grant.getUserId());
    }

    public Grant getGrantForUser(String userId) {
        final var user = users.getById(userId).orElseThrow();
        return getGrantForUser(user);
    }

    public Grant getGrantForUser(User user) {

        final var grant = new Grant();

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

        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }


    private Optional<User> getUserByUsername(String usernameOrEmail) {
        return users.findLimited(1, eq("username", usernameOrEmail)).stream().findFirst();
    }


}
