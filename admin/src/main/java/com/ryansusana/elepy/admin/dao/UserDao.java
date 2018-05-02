package com.ryansusana.elepy.admin.dao;

import com.mongodb.DB;
import com.ryansusana.elepy.admin.models.User;
import com.ryansusana.elepy.dao.MongoDao;
import org.jongo.Mapper;

import java.util.Optional;

public class UserDao extends MongoDao<User> {
    public UserDao(DB db, Mapper objectMapper) {
        super(db, "users", objectMapper, User.class);
    }


    public Optional<User> getByUsernameOrEmail(String usernameOrEmail) {
        return Optional.ofNullable(collection().findOne("{$or:[{username: #}, {email: #}]}", usernameOrEmail, usernameOrEmail).as(getClassType()));
    }

    public long count() {
        return collection().count();
    }
}
