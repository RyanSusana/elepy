package com.elepy.firebase;

import com.elepy.Elepy;
import com.elepy.tests.basic.BasicFunctionalityTest;
import com.google.api.core.ApiFutures;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FirestoreOptions;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

@Testcontainers
public class FirestoreBasicFunctionalityTest extends BasicFunctionalityTest {
    @Container
    public static GenericContainer firestoreContainer = new GenericContainer(
            new ImageFromDockerfile()
                    .withFileFromClasspath("Dockerfile", "firebase/Dockerfile")
                    .withFileFromClasspath("firebase.json", "firebase/firebase.json")
                    .withFileFromClasspath(".firebaserc", "firebase/.firebaserc")
    )
            .withExposedPorts(8080)
            .waitingFor(new HttpWaitStrategy().forPort(8080))
            .withStartupTimeout(Duration.ofSeconds(60));


    @BeforeAll
    static void startContainers() {
        firestoreContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        firestoreContainer.stop();
    }
    @Override
    public void configureElepy(Elepy elepy) {
        var host = String.format("%s:%d", firestoreContainer.getHost(), firestoreContainer.getMappedPort(8080));
        var firestore = FirestoreOptions.newBuilder()
                .setChannelProvider(InstantiatingGrpcChannelProvider.newBuilder()
                        .setEndpoint(host)
                        .setChannelConfigurator(
                                ManagedChannelBuilder::usePlaintext)
                        .build())
                .setCredentials(new FirestoreOptions.EmulatorCredentials())
                .setProjectId("dummy-project")
                .build().getService();

        var deletes =
                StreamSupport.stream(firestore.listCollections().spliterator(), false)
                        .flatMap(collection -> StreamSupport.stream(collection.listDocuments().spliterator(), false))
                        .map(DocumentReference::delete)
                        .toList();

        try {
            ApiFutures.allAsList(deletes).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        elepy.addConfiguration(FirestoreConfiguration.of(firestore));
    }

    @Disabled("Disabled because Firestore does not support case-insensitive queries.")
    @Test
    @Override
    public void can_FilterAndSearchItems_AsIntended() throws IOException, InterruptedException {
        super.can_FilterAndSearchItems_AsIntended();
    }

    @Disabled("Disabled because Firestore does not support case-insensitive queries.")
    @Test
    @Override
    public void canNot_FindItems_when_QueryDoesntMatch() throws IOException, InterruptedException {
        super.canNot_FindItems_when_QueryDoesntMatch();
    }

    @Disabled("Disabled because Firestore does not support case-insensitive queries.")
    @Test
    @Override
    public void can_SearchItems_as_Intended() throws IOException, InterruptedException {
        super.can_SearchItems_as_Intended();
    }
}
