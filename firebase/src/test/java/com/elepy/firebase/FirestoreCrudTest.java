package com.elepy.firebase;

import com.elepy.Elepy;
import com.elepy.tests.dao.FiltersTest;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.FirestoreOptions;
import org.junit.jupiter.api.Disabled;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@Testcontainers
public class FirestoreCrudTest extends FiltersTest {

    @Container
    public GenericContainer firestoreContainer = new GenericContainer(
            new ImageFromDockerfile()
                    .withFileFromClasspath("Dockerfile", "firebase/Dockerfile")
                    .withFileFromClasspath("firebase.json", "firebase/firebase.json")
                    .withFileFromClasspath(".firebaserc", "firebase/.firebaserc")
    )
            .withExposedPorts(8080)
            .waitingFor(new HttpWaitStrategy().forPort(8080))
            .withStartupTimeout(Duration.ofSeconds(60));

    @Override
    public void configureElepy(Elepy elepy) {
        var firestore = FirestoreOptions.newBuilder().setCredentials(NoCredentials.getInstance())
                .setProjectId("dummy-project")
                .setEmulatorHost(String.format("%s:%d", firestoreContainer.getHost(), firestoreContainer.getMappedPort(8080)))
                .setHost(String.format("%s:%s", firestoreContainer.getHost(), firestoreContainer.getMappedPort(8080)))
                .build().getService();

        elepy.addConfiguration(FirestoreConfiguration.of(firestore));
    }

    @Override
    @Disabled("Firestore can't search, behavior uses equals instead of case-insensitive search")
    public void canSearch() {
    }
}
