package com.elepy.firebase;

import com.elepy.dao.Crud;
import com.elepy.dao.CrudFactory;
import com.elepy.di.ElepyContext;
import com.elepy.models.Schema;
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
