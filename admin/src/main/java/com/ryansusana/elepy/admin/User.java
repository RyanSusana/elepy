package com.ryansusana.elepy.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ryansusana.elepy.annotations.PrettyName;
import com.ryansusana.elepy.annotations.RestModel;
import com.ryansusana.elepy.annotations.Searchable;
import com.ryansusana.elepy.concepts.IdProvider;
import com.ryansusana.elepy.models.RestModelAccessType;
import org.jongo.marshall.jackson.oid.MongoId;

@RestModel(slug = "/users", name = "Users", icon = "users", findAll = RestModelAccessType.ADMIN, findOne = RestModelAccessType.ADMIN, create = RestModelAccessType.ADMIN, updateRoute = UserUpdate.class)
public class User {

    @MongoId
    private final String id;

    @Searchable
    @JsonProperty("username")
    @PrettyName("Username")
    private final String username;
    private final String password;

    @Searchable
    @JsonProperty("email")
    @PrettyName("E-mail address")
    private final String email;

    @JsonCreator
    public User(@JsonProperty("_id") String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("email") String email) {
        this.id = id == null ? IdProvider.getRandomHexString(8) : id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    User hashWord() {
        return new User(id, username, BCrypt.hashpw(password, BCrypt.gensalt()), email);
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
}
