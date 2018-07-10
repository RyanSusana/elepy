package com.elepy.admin.models;

import com.elepy.admin.services.*;
import com.elepy.annotations.*;
import com.elepy.dao.SortOption;
import com.elepy.models.AccessLevel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


@Delete(handler = UserDelete.class)
@Find(accessLevel = AccessLevel.ADMIN)
@Create(accessLevel = AccessLevel.ADMIN, handler = UserCreate.class)
@Update(handler = UserUpdate.class)
@Evaluators({UserEvaluator.class})
@RestModel(
        //The only 2 required properties in RestModels are are slug and name
        slug = "/users",
        name = "Users",

        //Fontawesome icon from the free cdn
        icon = "users",

        description = "",

        //Sort
        //The default sorted mongo field. default is "_id"
        defaultSortField = "username",
        //Ascending sort or descending sort
        defaultSortDirection = SortOption.ASCENDING
)
public class User {

    //The only MUST-HAVE annotation is atleast one @Identifier used by Elepy to generate ID's for resources
    @Identifier
    private final String id;

    //This specifies that the property must be unique
    @Unique
    //Only this is necessary to search for users with a with a username
    @Searchable
    //How the username gets saved in the database
    @JsonProperty("username")
    //A nice looking name for the admin UI :)
    @PrettyName("Username")
    @Text(maximumLength = 30)
    private final String username;

    @PrettyName("Password")
    @JsonProperty("password")
    @Importance(-1)
    private final String password;

    @Searchable
    @JsonProperty("email")
    @PrettyName("E-mail address")
    @Unique
    private final String email;


    @Searchable
    @JsonProperty("user_type")
    @PrettyName("User role")
    private final UserType userType;


    @JsonCreator
    public User(@JsonProperty("_id") String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("email") String email, @JsonProperty("user_type") UserType userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userType = userType == null ? UserType.USER : userType;
    }

    public User hashWord() {
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
