package com.elepy.auth.users;

import com.elepy.auth.authentication.Credentials;
import com.elepy.crud.Crud;
import com.elepy.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static com.elepy.query.Filters.any;
import static com.elepy.query.Filters.eq;

@ApplicationScoped
public class UserCenter {

    @Inject
    private Crud<User> users;


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
