package com.elepy.firebase;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;
import com.google.cloud.firestore.Firestore;

public class FirestoreConfiguration implements Configuration {
    private final Firestore firestore;

    FirestoreConfiguration(Firestore firestore) {
        this.firestore = firestore;
    }

    public static FirestoreConfiguration of(Firestore firestore) {
        return new FirestoreConfiguration(firestore);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.registerDependency(Firestore.class, firestore);
        elepy.withDefaultCrudFactory(FirestoreCrudFactory.class);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
