package com.elepy.auth;

import com.elepy.annotations.*;
import com.elepy.auth.users.*;
import com.elepy.dao.SortOption;
import com.elepy.models.TextType;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Model(
        path = "/users",
        name = "Users",
        defaultSortField = "username",
        defaultSortDirection = SortOption.ASCENDING
)
@Entity(name = "elepy_user")
@Table(name = "elepy_users")

// Required permission is handled in UserUpdate.class
@Create(handler = UserCreate.class, requiredPermissions = {})
@Find(findManyHandler = UserFind.class,
        findOneHandler = UserFind.class,
        requiredPermissions = Permissions.CAN_ADMINISTRATE_USERS
)

// Required permission is handled in UserUpdate.class
@Update(handler = UserUpdate.class)
@Delete(handler = UserDelete.class, requiredPermissions = Permissions.CAN_ADMINISTRATE_USERS)
@Evaluators(UserEvaluator.class)
public class User {

    @Identifier
    @Id
    @PrettyName("User ID")
    @JsonProperty("id")
    @Importance(1)
    private String id;

    @Unique
    @Searchable
    @JsonProperty("username")
    @PrettyName("Username")
    @Importance(1)
    @Size(max = 30)
    private String username;


    @ElementCollection
    @CollectionTable(name = "elepy_user_permissions", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @PrettyName("Permissions")
    @JsonProperty("permissions")
    private List<String> permissions = new ArrayList<>();


    @PrettyName("Password")
    @JsonProperty("password")
    @Importance(-1)
    @Input(type = "password")
    private String password;


    public User() {

    }

    public User(String id, String username, String password, List<String> permissions) {
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
        this.setPassword("");
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
