package com.elepy.describers;

public class WeakRecursiveIndirection {
    private WeakRecursiveObject recursiveObject;

    public WeakRecursiveObject getRecursiveObject() {
        return recursiveObject;
    }

    public void setRecursiveObject(WeakRecursiveObject recursiveObject) {
        this.recursiveObject = recursiveObject;
    }
}
