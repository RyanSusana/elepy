package com.elepy.admin.models;

public interface UserInterface {

    String getUsername();

    String getPassword();

    boolean passwordEquals(String otherPassword);

}
