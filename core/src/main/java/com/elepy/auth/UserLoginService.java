package com.elepy.auth;


import com.elepy.dao.Crud;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UserLoginService {

    private final Crud<User> userDao;

    public UserLoginService(Crud<User> userDao) {
        this.userDao = userDao;
    }


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

        final List<? extends User> users = userDao.searchInField("email", usernameOrEmail);
        if (users.size() > 0) {
            return Optional.of(users.get(0));
        }

        return Optional.empty();
    }

}