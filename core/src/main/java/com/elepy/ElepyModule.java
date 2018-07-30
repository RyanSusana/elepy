package com.elepy;


import spark.Service;

public abstract class ElepyModule {

    private Elepy elepy;
    private Service http;

    public ElepyModule() {

    }

    public ElepyModule(Elepy inst, Service http) {
        this.elepy = inst;
        this.http = http;
    }

    public ElepyModule(Elepy inst) {
        this.elepy = inst;
        this.http = elepy.http();
    }

    public abstract void setup(Service http, Elepy elepy);

    public abstract void routes(Service http);


}
