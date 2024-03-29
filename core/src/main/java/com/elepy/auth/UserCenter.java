package com.elepy.auth;

import jakarta.inject.Inject;
import com.elepy.dao.Crud;
import com.elepy.utils.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static com.elepy.dao.Filters.*;

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
