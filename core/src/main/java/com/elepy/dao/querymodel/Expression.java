package com.elepy.dao.querymodel;

public abstract class Expression {

    public boolean canBeIgnored() {
        return false;
    }

    public void purge(){

    }
} 
