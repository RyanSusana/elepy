package com.elepy.graphql;

import com.elepy.models.ModelContext;
import graphql.ErrorClassification;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class CrudDataFetcher<T> implements DataFetcher<List<T>> {
    private final ModelContext<T> modelContext;

    public CrudDataFetcher(ModelContext<T> crud) {
        this.modelContext = crud;
    }

    @Override
    public List<T> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

        return modelContext.getCrud().getAll();
    }
}
