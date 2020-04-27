package com.elepy.auth;

import java.util.ArrayList;
import java.util.List;


public class Role {

    private String id;

    private String name;

    private String description;

    private List<String> permissions = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
