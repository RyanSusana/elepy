package com.ryansusana.elepy;


import spark.Service;

public abstract class ElepyModule {

    private final Elepy elepy;
    private final Service http;

    public ElepyModule(Elepy inst, Service http) {
        this.elepy = inst;
        this.http = http;
    }

    public ElepyModule(Elepy inst) {
        this.elepy = inst;
        this.http = elepy.http();
    }

    public Elepy elepy() {
        return elepy;
    }

    public Service http() {
        return http;
    }

    public abstract void setup();

    public abstract void routes();


}
