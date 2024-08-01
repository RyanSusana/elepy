package com.elepy.dao;

public abstract class Expression {

    public boolean canBeIgnored() {
        return false;
    }

    public void purge(){

    }
} 
