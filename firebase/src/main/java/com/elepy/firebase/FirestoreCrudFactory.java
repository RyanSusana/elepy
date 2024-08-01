package com.elepy.firebase;

import com.elepy.crud.Crud;
import com.elepy.crud.CrudFactory;
import com.elepy.di.ElepyContext;
import com.elepy.schemas.Schema;
import com.google.cloud.firestore.Firestore;
import jakarta.inject.Inject;

public class FirestoreCrudFactory implements CrudFactory {

    @Inject
    private ElepyContext elepyContext;

    @Override
    public <T> Crud<T> crudFor(Schema<T> type) {
        return new FirestoreCrud<>(elepyContext.getDependency(Firestore.class), type);
    }
}
