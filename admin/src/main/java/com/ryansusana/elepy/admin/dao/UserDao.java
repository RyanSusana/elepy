package com.ryansusana.elepy.admin.dao;

import com.mongodb.DB;
import com.ryansusana.elepy.admin.models.User;
import com.ryansusana.elepy.dao.MongoDao;
import org.jongo.Mapper;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserDao extends MongoDao<User> {
    public UserDao(DB db, Mapper objectMapper) {
        super(db, "users", objectMapper, User.class);
    }


    public Optional<User> getByUsernameOrEmail(String usernameOrEmail) {
        return Optional.ofNullable(collection().findOne("{$or:[{username: #}, {email: #}]}", Pattern.compile(usernameOrEmail, Pattern.CASE_INSENSITIVE), Pattern.compile(usernameOrEmail, Pattern.CASE_INSENSITIVE)).as(getClassType()));
    }

    public long count() {
        return collection().count();
    }
}
