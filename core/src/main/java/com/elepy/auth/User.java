package com.elepy.auth;

import com.elepy.annotations.*;
import com.elepy.auth.users.UserCreate;
import com.elepy.auth.users.UserDelete;
import com.elepy.auth.users.UserFind;
import com.elepy.auth.users.UserUpdate;
import com.elepy.dao.SortOption;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@PredefinedRole(id = "owner", name = "Owner", permissions = {"owner", "*"})
@PredefinedRole(id = "admin", name = "Admin", permissions = {"*"})
@Model(
        path = "/users",
        name = "{elepy.models.users.fields.users.label}",
        defaultSortField = "username",
        defaultSortDirection = SortOption.ASCENDING
)
@Entity(name = "elepy_user")
@Table(name = "elepy_users")

// Required permission is handled in UserCreate.class
@Create(handler = UserCreate.class, requiredPermissions = {})

@Find(findManyHandler = UserFind.class,
        findOneHandler = UserFind.class,
        requiredPermissions = "users.find"
)

// Required permission is handled in UserUpdate.class
@Update(handler = UserUpdate.class)
@Delete(handler = UserDelete.class, requiredPermissions = "users.delete")
@UserPasswordValidator
public class User {

    @Identifier
    @Id
    @Label("{elepy.models.users.fields.id.label}")
    @JsonProperty("id")
    @Importance(1)
    private String id;

    @Unique
    @Searchable
    @JsonProperty("username")
    @Label("{elepy.models.users.fields.username.label}")
    @Importance(1)
    @Size(max = 300, min = 4)
    @Featured
    @Pattern(regexp = "(^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$)|([a-zA-Z0-9\\.\\-]+)", message = "{elepy.models.users.exceptions.username}")
    private String username;


    @Label("{elepy.models.users.fields.password.label}")
    @JsonProperty("password")
    @Importance(-1)
    @Input(type = "password")
    private String password;

    @ElementCollection
    @CollectionTable(name = "elepy_user_roles", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "roles")
    @Label("{elepy.models.users.fields.roles.label}")
    @JsonProperty("roles")
    private List<@Reference(to = Role.class) String> roles = new ArrayList<>();


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public User() {

    }

    public User(String id, String username, String password, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
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

    /**
     * Method to make sure that all email addresses are lowercase
     */
    public void cleanUsername() {
        if (username.contains("@")) {
            username = username.toLowerCase();
        }
    }
}
