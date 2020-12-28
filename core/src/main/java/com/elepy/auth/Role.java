package com.elepy.auth;

import com.elepy.annotations.*;
import com.elepy.auth.roles.*;
import com.elepy.dao.SortOption;
import com.elepy.id.SlugIdentityProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Model(
        path = "/roles",
        name = "{elepy.messages.roles.roles}",
        defaultSortField = "name",
        defaultSortDirection = SortOption.ASCENDING
)
@Find(requiredPermissions = "roles.find", findManyHandler = RolesFind.class, findOneHandler = RolesFindOne.class)
@Create(requiredPermissions = "roles.create", handler = RolesCreate.class)
@Update(requiredPermissions = "roles.update", handler = RolesUpdate.class)
@Delete(requiredPermissions = "roles.delete", handler = RolesDelete.class)
@Entity(name = "elepy_role")
@Table(name = "elepy_roles")
@IdProvider(SlugIdentityProvider.class)
public class Role {

    @Identifier
    @Id
    @Label("{elepy.messages.roles.id}")
    @JsonProperty("id")
    @Importance(1)
    private String id;

    @Unique
    @Searchable
    @JsonProperty("name")
    @Label("{elepy.messages.roles.name}")
    @Importance(1)
    @Size(max = 30)
    private String name;

    @Searchable
    @JsonProperty("description")
    @Label("{elepy.messages.roles.description}")
    @Importance(1)
    @TextArea
    private String description;

    @ElementCollection
    @CollectionTable(name = "elepy_role_permissions", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @Label("{elepy.messages.roles.permissions}")
    @JsonProperty("permissions")
    private List<String> permissions = new ArrayList<>();


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getPermissions() {
        return permissions;
    }


    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
