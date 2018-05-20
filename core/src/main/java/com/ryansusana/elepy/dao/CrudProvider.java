package com.ryansusana.elepy.dao;

import com.ryansusana.elepy.Elepy;

public abstract class CrudProvider<T> {
    private Elepy elepy;


    public Elepy elepy() {
        return elepy;
    }
    public CrudProvider setElepy(Elepy elepy){
        this.elepy = elepy;
        return this;
    }

    public abstract Crud<T> crudFor(Class<T> type);
}
