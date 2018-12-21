package com.elepy;


import spark.Service;

public interface ElepyModule {


    void setup(Service http, Elepy elepy);

    void routes(Service http, Elepy elepy);


}
