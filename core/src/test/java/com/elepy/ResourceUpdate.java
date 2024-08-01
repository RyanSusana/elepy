package com.elepy;

import com.elepy.crud.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.handlers.SimpleUpdate;
import com.elepy.http.Request;

public class ResourceUpdate extends SimpleUpdate<Resource> {


    private final ElepyContext elepyContext;


    public ResourceUpdate(ElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }


    @Override
    public void beforeUpdate(Resource beforeVersion, Resource updatedVersion, Request httpRequest, Crud<Resource> crud) throws Exception {

    }

    @Override
    public void afterUpdate(Resource beforeVersion, Resource updatedVersion, Crud<Resource> crud) {


    }
}
