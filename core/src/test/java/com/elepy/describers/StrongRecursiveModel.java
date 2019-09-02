package com.elepy.describers;

public class StrongRecursiveModel {
    private String id;

    private DirectRecursiveObject recursiveObject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DirectRecursiveObject getRecursiveObject() {
        return recursiveObject;
    }

    public void setRecursiveObject(DirectRecursiveObject recursiveObject) {
        this.recursiveObject = recursiveObject;
    }
}
