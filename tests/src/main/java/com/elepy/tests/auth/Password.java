package com.elepy.tests.auth;

import com.elepy.annotations.*;
import com.elepy.http.HttpMethod;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@PredefinedRole(id = "password-admin", name = "Password Admin", permissions = "passwords.*")
@PredefinedRole(id = "password-viewer", name = "Password Viewer", permissions = "passwords.view")
@Model(name = "All passwords", path = "/passwords")
@Create(requiredPermissions = "passwords.create")
@Update(requiredPermissions = "passwords.update")
@Delete(requiredPermissions = "passwords.delete")
@Find(requiredPermissions = "passwords.view")
@Action(name = "Delete all", path = "/custom-action", method = HttpMethod.POST, requiredPermissions = "passwords.delete", handler = DeleteAllPasswords.class)
@Entity(name = "passwords")
@Table(name = "passwords")
public class Password {

    @Id
    private String savedLocation;
    private String username;

    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getSavedLocation() {
        return savedLocation;
    }

    public void setSavedLocation(String savedLocation) {
        this.savedLocation = savedLocation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
