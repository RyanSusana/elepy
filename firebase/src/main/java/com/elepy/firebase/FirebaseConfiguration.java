package com.elepy.firebase;

import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

public class FirebaseConfiguration implements Configuration {

    private final FirebaseApp app;
    private final boolean enableFirestore;
    private final String bucketName;

    FirebaseConfiguration(FirebaseApp app, boolean enableFirestore, String bucketName) {
        this.app = app;
        this.enableFirestore = enableFirestore;
        this.bucketName = bucketName;
    }

    public static FirebaseConfiguration withFirestoreAndCloudStorage(FirebaseApp app, String bucketName) {
        return new FirebaseConfiguration(app, true, bucketName);
    }

    public static FirebaseConfiguration withCloudStorage(FirebaseApp app, String bucketName) {
        return new FirebaseConfiguration(app, false, bucketName);
    }

    public static FirebaseConfiguration withFirestore(FirebaseApp app) {
        return new FirebaseConfiguration(app, true, null);
    }


    @Override
    public void preConfig(ElepyPreConfiguration elepy) {

        if (enableFirestore) {
            elepy.registerDependency(Firestore.class, FirestoreClient.getFirestore(app));
            elepy.withDefaultCrudFactory(FirestoreCrudFactory.class);
        }

        if (bucketName != null) {
            StorageClient client = StorageClient.getInstance(app);
            elepy.withUploads(new CloudStorageFileService(client.bucket(bucketName).getStorage(), bucketName));
        }
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {

    }
}
