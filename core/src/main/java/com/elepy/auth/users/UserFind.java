package com.elepy.auth.users;

import com.elepy.auth.User;
import com.elepy.dao.Crud;
import com.elepy.http.Request;
import com.elepy.handlers.MappedFind;

public class UserFind extends MappedFind<User, User> {
    @Override
    public User map(User user, Request request, Crud<User> crud) {
        return user.withEmptyPassword();
    }
}
