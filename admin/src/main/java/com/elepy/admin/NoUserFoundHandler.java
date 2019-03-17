package com.elepy.admin;

import com.elepy.admin.models.UserInterface;
import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;

public interface NoUserFoundHandler {

    void handle(HttpContext context, Crud<UserInterface> crud);
}
