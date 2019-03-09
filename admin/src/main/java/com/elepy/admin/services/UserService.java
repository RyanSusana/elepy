package com.elepy.admin.services;

import com.elepy.admin.models.UserInterface;
import com.elepy.dao.Crud;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final Crud<? extends UserInterface> userDao;

    @java.beans.ConstructorProperties({"userDao"})
    public UserService(Crud<? extends UserInterface> userDao) {
        this.userDao = userDao;
    }


    public Optional<UserInterface> login(String usernameOrEmail, String password) {
        Optional<UserInterface> user = getUser(usernameOrEmail);

        if (user.isPresent() && user.get().passwordEquals(password)) {
            return user;
        }
        return Optional.empty();
    }

    private Optional<UserInterface> getUser(String usernameOrEmail) {
        if (!usernameOrEmail.contains("@")) {
            final List<? extends UserInterface> users = userDao.searchInField("username", usernameOrEmail);
            if (users.size() > 0) {
                return Optional.of(users.get(0));
            }
        }

        final List<? extends UserInterface> users = userDao.searchInField("email", usernameOrEmail);
        if (users.size() > 0) {
            return Optional.of(users.get(0));
        }

        return Optional.empty();
    }

}
