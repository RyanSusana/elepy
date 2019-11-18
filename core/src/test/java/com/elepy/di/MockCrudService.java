package com.elepy.di;

import com.elepy.annotations.Inject;

public class MockCrudService {

    @Inject
    private MockCrudResource crud;

    public MockCrudResource getCrud() {
        return crud;
    }

    public void setCrud(MockCrudResource crud) {
        this.crud = crud;
    }



}
