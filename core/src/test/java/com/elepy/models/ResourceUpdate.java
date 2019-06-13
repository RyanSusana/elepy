package com.elepy.models;

import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import com.elepy.http.Request;
import com.elepy.routes.SimpleUpdate;

public class ResourceUpdate extends SimpleUpdate<Resource> {


    private final ElepyContext elepyContext;


    public ResourceUpdate(ElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }


    @Override
    public void beforeUpdate(Resource beforeVersion, Request request, Crud<Resource> crud) throws Exception {

    }

    @Override
    public void afterUpdate(Resource beforeVersion, Resource updatedVersion, Crud<Resource> crud) {


    }
}
