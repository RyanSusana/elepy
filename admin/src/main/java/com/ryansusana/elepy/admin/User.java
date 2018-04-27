package com.ryansusana.elepy.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryansusana.elepy.annotations.PrettyName;
import com.ryansusana.elepy.annotations.RestModel;
import com.ryansusana.elepy.annotations.Searchable;
import com.ryansusana.elepy.annotations.Unique;
import com.ryansusana.elepy.models.RestModelAccessType;
import org.jongo.marshall.jackson.oid.MongoId;

@RestModel(slug = "/users", name = "Users", icon = "users", deleteRoute = UserDelete.class, createRoute = UserCreate.class, findAll = RestModelAccessType.ADMIN, findOne = RestModelAccessType.ADMIN, create = RestModelAccessType.ADMIN, updateRoute = UserUpdate.class)
public class User {

    @MongoId
    private final String id;

    @Searchable
    @JsonProperty("username")
    @PrettyName("Username")
    @Unique
    private final String username;

    @PrettyName("Password")
    @JsonProperty("password")
    private final String password;

    @Searchable
    @JsonProperty("email")
    @PrettyName("E-mail address")
    @Unique
    private final String email;


    @Searchable
    @JsonProperty("user_type")
    @PrettyName("user_role")
    private final UserType userType;

    @JsonCreator
    public User(@JsonProperty("_id") String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("email") String email, @JsonProperty("user_type") UserType userType) {
        this.id =  id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userType = userType == null ? UserType.USER : userType;
    }

    User hashWord() {
        return new User(id, username, BCrypt.hashpw(password, BCrypt.gensalt()), email, userType);
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public UserType getUserType() {
        return userType;
    }
}
