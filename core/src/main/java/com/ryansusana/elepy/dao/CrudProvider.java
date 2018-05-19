package com.ryansusana.elepy.dao;

import com.ryansusana.elepy.Elepy;

public abstract class CrudProvider {
    private Elepy elepy;


    public Elepy elepy() {
        return elepy;
    }
    public CrudProvider setElepy(Elepy elepy){
        this.elepy = elepy;
        return this;
    }

    public abstract <T> Crud<T> crudFor(Class<T> type);
}
