package com.elepy.tests;

import com.elepy.auth.User;

import javax.persistence.Entity;

@Entity

public class CustomUser extends User {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
