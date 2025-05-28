package com.elepy.firebase;

import com.elepy.Elepy;
import com.elepy.query.FilterType;
import com.elepy.tests.Product;
import com.elepy.tests.dao.FiltersTest;
import com.google.api.core.ApiFutures;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FirestoreOptions;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class FirestoreFiltersTest extends FiltersTest {

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

    @Override
    @Test
    public void canSearch() {
        // Firestore does not support searching, so we use startsWith instead.
        seedWithProducts(
                Product.withDescription("test"),
                Product.withDescription("test2"),
                Product.withDescription("test3"),
                Product.withDescription("test4"),
                Product.withDescription("test5"),
                Product.withDescription("can't find this test unfortunately... :(")
        );

        var foundProducts = executeQuery(Map.of("q", "test"));
        assertThat(foundProducts).hasSize(5);

    }

    @Override
    @Test
    @Disabled("Firestore can't filter on contains.")
    public void canFilter_CONTAINS_onString() {

    }

    @Override
    @Test
    public void canFilter_GREATER_THAN_onNumber() {
        // Firestore does not support BigDecimal.

        Product product = new Product();
        product.setNumber(10);

        seedWithProducts(product);


        assertThat(executeFilter("number", FilterType.GREATER_THAN, 5))
                .hasSize(1);
        assertThat(executeFilter("number", FilterType.GREATER_THAN, 15))
                .isEmpty();
        assertThat(executeFilter("number", FilterType.GREATER_THAN, 10))
                .isEmpty();
    }

    @Override
    @Test
    public void canFilter_GREATER_THAN_OR_EQUALS_onNumber() {
        // Firestore does not support BigDecimal.

        Product product = new Product();
        product.setNumber(10);

        seedWithProducts(product);


        assertThat(executeFilter("number", FilterType.GREATER_THAN_OR_EQUALS, 5))
                .hasSize(1);
        assertThat(executeFilter("number", FilterType.GREATER_THAN_OR_EQUALS, 15))
                .isEmpty();
        assertThat(executeFilter("number", FilterType.GREATER_THAN_OR_EQUALS, 10))
                .hasSize(1);
    }

    @Override
    @Test
    public void canFilter_LESSER_THAN_onNumber() {
        // Firestore does not support BigDecimal.

        Product product = new Product();
        product.setNumber(10);

        seedWithProducts(product);
        assertThat(executeFilter("number", FilterType.LESSER_THAN, 15))
                .hasSize(1);
        assertThat(executeFilter("number", FilterType.LESSER_THAN, 5))
                .isEmpty();
        assertThat(executeFilter("number", FilterType.LESSER_THAN, 10))
                .isEmpty();
    }

    @Override
    @Test
    public void canFilter_LESSER_THAN_OR_EQUALS_onNumber() {
        // Firestore does not support BigDecimal.

        Product product = new Product();
        product.setNumber(10);

        seedWithProducts(product);
        assertThat(executeFilter("number", FilterType.LESSER_THAN_OR_EQUALS, 15))
                .hasSize(1);
        assertThat(executeFilter("number", FilterType.LESSER_THAN_OR_EQUALS, 5))
                .isEmpty();
        assertThat(executeFilter("number", FilterType.LESSER_THAN_OR_EQUALS, 10))
                .hasSize(1);
    }

    @Override
    @Disabled("TODO")
    @Test
    public void canFilter_CONTAINS_onArray() {
        super.canFilter_CONTAINS_onArray();
    }
}
