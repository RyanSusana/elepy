package com.elepy.admin.models;

import com.elepy.admin.services.*;
import com.elepy.annotations.*;
import com.elepy.dao.SortOption;
import com.elepy.http.AccessLevel;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Delete(accessLevel = AccessLevel.PROTECTED, handler = UserDelete.class)
@Find(accessLevel = AccessLevel.PROTECTED, findManyHandler = UserFind.class, findOneHandler = UserFind.class)
@Create(handler = UserCreate.class, accessLevel = AccessLevel.PUBLIC)
@Update(accessLevel = AccessLevel.PROTECTED, handler = UserUpdate.class)
@Evaluators({UserEvaluator.class})
@RestModel(
        slug = "/users",
        name = "Users",
        defaultSortField = "username",
        defaultSortDirection = SortOption.ASCENDING
)
@Entity(name = "elepy_user")
@Table(name = "elepy_users")
public class User implements UserInterface {

    @Identifier
    @Id
    private String id;

    @Unique
    @Searchable
    @JsonProperty("username")
    @PrettyName("Username")
    @Text(maximumLength = 30)
    private String username;

    @PrettyName("Password")
    @JsonProperty("password")
    @Importance(-1)
    @Text(TextType.PASSWORD)
    private String password;

    @Searchable
    @JsonProperty("email")
    @PrettyName("E-mail address")
    @Unique
    private String email;


    @Searchable
    @JsonProperty("user_type")
    @PrettyName("User role")
    private UserType userType;


    public User() {

    }

    @JsonCreator
    public User(@JsonProperty("_id") String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("email") String email, @JsonProperty("user_type") UserType userType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userType = userType == null ? UserType.USER : userType;
    }

    @Transient
    public User hashWord() {
        return new User(id, username, BCrypt.hashpw(password, BCrypt.gensalt()), email, userType);
    }

    @Transient
    public User withEmptyPassword() {
        return new User(id, username, "", email, userType);
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean passwordEquals(String otherPassword) {
        return BCrypt.checkpw(otherPassword, password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
