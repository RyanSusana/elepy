package com.elepy.admin.services;

import com.elepy.admin.models.User;
import com.elepy.admin.models.UserType;
import com.elepy.dao.Crud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final Crud<User> userDao;

    @java.beans.ConstructorProperties({"userDao"})
    public UserService(Crud<User> userDao) {
        this.userDao = userDao;
    }


    public Optional<User> login(String usernameOrEmail, String password) {
        if (userDao.count() == 0) {
            User user = new User(null, "admin", BCrypt.hashpw("admin", BCrypt.gensalt()), "pleasePutAdminEmailHere@elepy.com", UserType.SUPER_ADMIN);
            userDao.create(user);
        }
        Optional<User> user = getUser(usernameOrEmail);

        if (user.isPresent() && BCrypt.checkpw(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    private Optional<User> getUser(String usernameOrEmail) {
        try {
            if (!usernameOrEmail.contains("@")) {
                final Field username = User.class.getDeclaredField("username");
                username.setAccessible(true);
                final List<User> users = userDao.searchInField(username, usernameOrEmail);
                if (users.size() > 0) {
                    return Optional.of(users.get(0));
                }
            }


            final Field email = User.class.getDeclaredField("email");
            email.setAccessible(true);
            final List<User> users = userDao.searchInField(email, usernameOrEmail);
            if (users.size() > 0) {
                return Optional.of(users.get(0));
            }
        } catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

}
