package com.elepy.auth.authorization;

import com.elepy.annotations.*;
import com.elepy.id.SlugIdentityProvider;
import com.elepy.query.SortOption;

import javax.persistence.*;

@Hidden
@Model(
        path = "/policies",
        name = "Policies",
        defaultSortField = "name",
        defaultSortDirection = SortOption.ASCENDING
)
@Find(requiredPermissions = "policies.find")
@Create(requiredPermissions = "policies.create")
@Update(requiredPermissions = "policies.update")
@Delete(requiredPermissions = "policies.delete")
@Entity(name = "elepy_policies")
@Table(name = "elepy_policies", indexes = {@Index(columnList = "target")})
@IdProvider(SlugIdentityProvider.class)
public class PolicyBinding {

    @Identifier
    @Id
    private String id;
    private String principal;
    private String role;
    @Column(name = "target")
    private String target;

    public String getId() {
        return id;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getTarget() {
        return target;
    }

    public String getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTarget(String target) {
        this.target = target;
    }


}
