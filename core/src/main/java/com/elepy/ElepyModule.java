package com.elepy;


import spark.Service;

public abstract class ElepyModule {

    private Elepy elepy;
    private Service http;

    public ElepyModule(){

    }
    public ElepyModule(Elepy inst, Service http) {
        this.elepy = inst;
        this.http = http;
    }

    public ElepyModule(Elepy inst) {
        this.elepy = inst;
        this.http = elepy.http();
    }

    public void setElepy(Elepy elepy) {
        this.elepy = elepy;
    }

    public void setHttp(Service http) {
        this.http = http;
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
