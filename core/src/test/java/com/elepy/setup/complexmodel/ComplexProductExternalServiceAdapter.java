package com.elepy.setup.complexmodel;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;

public class ComplexProductExternalServiceAdapter {

    @Inject
    private Crud<ComplexProduct> crud;

    private int amountOfProductChanges;

    public void addOneToChanges() {
        amountOfProductChanges += 1;
    }

    public int getAmountOfProductChanges() {
        return amountOfProductChanges;
    }
}
