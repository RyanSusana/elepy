package com.elepy.auth;

import com.elepy.annotations.*;
import com.elepy.dao.SortOption;
import com.elepy.id.SlugIdentityProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Model(
        path = "/roles",
        name = "Policy",
        defaultSortField = "name",
        defaultSortDirection = SortOption.ASCENDING
)

@PredefinedRole(name = "Role Editor", permissions = "roles.*")
@Find(requiredPermissions = "roles.find")
@Create(requiredPermissions = "roles.create")
@Update(requiredPermissions = "roles.update")
@Delete(requiredPermissions = "roles.delete")
@Entity(name = "elepy_role")
@Table(name = "elepy_roles")
@IdProvider(SlugIdentityProvider.class)
public class CustomRole extends Role {

    @Identifier
    @Id
    @PrettyName("Role ID")
    @JsonProperty("id")
    @Importance(1)
    private String id;

    @Unique
    @Searchable
    @JsonProperty("name")
    @PrettyName("Name")
    @Importance(1)
    @Size(max = 30)
    private String name;

    @Searchable
    @JsonProperty("description")
    @Importance(1)
    @TextArea
    @PrettyName("Description")
    private String description;

    @ElementCollection
    @CollectionTable(name = "elepy_role_permissions", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "permission")
    @PrettyName("Permissions")
    @JsonProperty("permissions")
    private List<String> permissions = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
