package com.elepy.auth;

import com.elepy.annotations.*;
import com.elepy.auth.users.*;
import com.elepy.dao.SortOption;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@RestModel(
        slug = "/users",
        name = "Users",
        defaultSortField = "username",
        defaultSortDirection = SortOption.ASCENDING
)
@Entity(name = "elepy_user")
@Table(name = "elepy_users")
@Create(handler = UserCreate.class, requiredPermissions = {})
@Find(findManyHandler = UserFind.class,
        findOneHandler = UserFind.class,
        requiredPermissions = Permissions.LOGGED_IN
)
@Update(handler = UserUpdate.class)
@Delete(handler = UserDelete.class)
@Evaluators(UserEvaluator.class)
public class User {

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

    @ElementCollection
    @CollectionTable(name = "elepy_user_permissions", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    private List<String> permissions = new ArrayList<>();


    public User() {

    }

    @JsonCreator
    public User(@JsonProperty("id") String id, @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("permissions") List<String> permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        return permissions;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public User withEmptyPassword() {
        return new User(id, username, "", permissions);
    }
}
