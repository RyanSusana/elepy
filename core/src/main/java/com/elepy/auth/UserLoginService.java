package com.elepy.auth;


import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UserLoginService {

    @Inject
    private Crud<User> userDao;

    public Optional<User> login(String usernameOrEmail, String password) {
        Optional<User> user = getUser(usernameOrEmail);

        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    private Optional<User> getUser(String usernameOrEmail) {
        if (!usernameOrEmail.contains("@")) {
            final List<? extends User> users = userDao.searchInField("username", usernameOrEmail);
            if (users.size() > 0) {
                return Optional.of(users.get(0));
            }
        }

        return Optional.empty();
    }

}