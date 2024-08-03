package com.elepy.auth.users;

import com.elepy.crud.Crud;
import com.elepy.handlers.MappedFind;
import com.elepy.http.Request;

public class UserFind extends MappedFind<User, User> {
    @Override
    public User map(User user, Request request, Crud<User> crud) {
        return user.withEmptyPassword();
    }
}
