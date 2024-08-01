package com.elepy.query;

public abstract class Expression {

    public boolean canBeIgnored() {
        return false;
    }

    public void purge(){

    }
} 
