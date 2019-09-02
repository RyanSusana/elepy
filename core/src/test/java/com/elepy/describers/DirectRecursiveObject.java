package com.elepy.describers;

public class DirectRecursiveObject {

    private String textField;
    private DirectRecursiveObject recursiveObject;

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public DirectRecursiveObject getRecursiveObject() {
        return recursiveObject;
    }

    public void setRecursiveObject(DirectRecursiveObject recursiveObject) {
        this.recursiveObject = recursiveObject;
    }
}
