package com.elepy;


import com.elepy.di.ElepyContext;
import spark.Service;

public interface ElepyModule {


    void beforeElepyConstruction(Service http, Elepy elepy);

    void afterElepyConstruction(Service http, ElepyContext elepy);


}
