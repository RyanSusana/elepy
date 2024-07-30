package com.elepy.firebase;

import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;

public class FirestoreConfiguration implements Configuration {

    private final FirebaseApp app;

    FirestoreConfiguration(FirebaseApp app) {
        this.app = app;
    }

    public static FirestoreConfiguration of(FirebaseApp app) {
        return new FirestoreConfiguration(app);
    }

    @Override
    public void preConfig(ElepyPreConfiguration elepy) {
        elepy.registerDependency(Firestore.class, FirestoreClient.getFirestore(app));
        elepy.withDefaultCrudFactory(FirestoreCrudFactory.class);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
