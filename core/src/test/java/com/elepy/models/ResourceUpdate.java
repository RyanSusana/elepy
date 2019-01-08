package com.elepy.models;

import com.elepy.Elepy;
import com.elepy.concepts.Resource;
import com.elepy.dao.Crud;
import com.elepy.routes.SimpleUpdate;

public class ResourceUpdate extends SimpleUpdate<Resource> {
    @Override
    public void beforeUpdate(Resource beforeVersion, Crud<Resource> crud, Elepy elepy) throws Exception {

    }

    @Override
    public void afterUpdate(Resource beforeVersion, Resource updatedVersion, Crud<Resource> crud, Elepy elepy) {


    }
}
