package com.elepy.setup.complexmodel;

import com.elepy.Base;
import com.elepy.Elepy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.google.common.truth.Truth.assertThat;


class ComplexProductTest extends Base {

    private Elepy elepy;

    @BeforeEach
    void setUp() {
        elepy = createElepy();
    }

    @AfterEach
    void tearDown() {
        elepy.stop();
    }

    @Test
    void testComplexFunctionality() throws IOException, InterruptedException {

        elepy.addModel(ComplexProduct.class);
        elepy.start();
        final var adapter = elepy.getDependency(ComplexProductExternalServiceAdapter.class);


        final int begin = adapter.getAmountOfProductChanges();

        HttpClient client = HttpClient.newBuilder().build();

        client.send(HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:%d/complex-products", elepy.http().port())))
                .build(), HttpResponse.BodyHandlers
                .ofString()).body();


        assertThat(adapter.getAmountOfProductChanges())
                .isEqualTo(begin + 1);

    }


}