package com.elepy.admin.dao;

import com.elepy.admin.models.User;
import com.elepy.dao.MongoDao;
import com.mongodb.DB;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserDao extends MongoDao<User> {
    public UserDao(DB db) {
        super(db, "users", User.class);
    }


    public Optional<User> getByUsernameOrEmail(String usernameOrEmail) {
        if (!usernameOrEmail.contains("@")) {
            return Optional.ofNullable(collection().findOne("{username: #}", Pattern.compile(usernameOrEmail, Pattern.CASE_INSENSITIVE)).as(User.class));
        } else {
            return Optional.ofNullable(collection().findOne("{email: #}", Pattern.compile(usernameOrEmail, Pattern.CASE_INSENSITIVE)).as(User.class));
        }

    }

    public long count() {
        return collection().count();
    }
}
