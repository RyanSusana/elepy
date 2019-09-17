package com.elepy.setup.complexmodel;

import com.elepy.annotations.Inject;
import com.elepy.dao.Crud;
import com.elepy.handlers.FindManyHandler;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ComplexProductUpdate implements FindManyHandler<ComplexProduct> {

    @Inject
    private ComplexProductExternalServiceAdapter serviceAdapter;

    @Override
    public void handleFindMany(HttpContext context, Crud<ComplexProduct> crud, ModelContext<ComplexProduct> modelContext, ObjectMapper objectMapper) throws Exception {
        serviceAdapter.addOneToChanges();
    }
}
