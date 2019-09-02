package com.elepy.describers;

public class RecursiveObject {

    private String textField;
    private RecursiveObject recursiveObject;

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public RecursiveObject getRecursiveObject() {
        return recursiveObject;
    }

    public void setRecursiveObject(RecursiveObject recursiveObject) {
        this.recursiveObject = recursiveObject;
    }
}
