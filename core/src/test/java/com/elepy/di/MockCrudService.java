package com.elepy.di;

import com.elepy.Resource;
import com.elepy.annotations.ElepyConstructor;
import com.elepy.annotations.Inject;

public class MockCrudService {

    @Inject
    private MockCrudGeneric<Resource> crudGeneric;

    private final MockCrudResource crudResource;

    @ElepyConstructor
    public MockCrudService(@Inject MockCrudResource crudResource) {
        this.crudResource = crudResource;
    }

    public MockCrudGeneric<Resource> getCrudGeneric() {
        return crudGeneric;
    }

    public MockCrudResource getCrudResource() {
        return crudResource;
    }
}
