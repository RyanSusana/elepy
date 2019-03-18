package com.elepy.admin.services;

import com.elepy.admin.models.User;
import com.elepy.dao.Crud;
import com.elepy.http.Request;
import com.elepy.routes.MappedFind;

public class UserFind extends MappedFind<User, User> {
    @Override
    public User map(User user, Request request, Crud<User> crud) {
        return user.withEmptyPassword();
    }
}
