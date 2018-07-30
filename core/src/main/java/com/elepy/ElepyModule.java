package com.elepy;


import spark.Service;

public abstract class ElepyModule {


    public ElepyModule() {

    }


    public abstract void setup(Service http, Elepy elepy);

    public abstract void routes(Service http, Elepy elepy);


}
