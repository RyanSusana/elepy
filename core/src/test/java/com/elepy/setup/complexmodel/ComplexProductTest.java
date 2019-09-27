package com.elepy.setup.complexmodel;

import com.elepy.Base;
import com.elepy.Elepy;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;


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
    @Ignore("This should be moved to tests module")
    void testComplexFunctionality() throws IOException, InterruptedException {

//        elepy.addModel(ComplexProduct.class);
//        elepy.start();
//        final var adapter = elepy.getDependency(ComplexProductExternalServiceAdapter.class);
//
//
//        final int begin = adapter.getAmountOfProductChanges();
//
//        HttpClient client = HttpClient.newBuilder().build();
//
//        client.send(HttpRequest.newBuilder()
//                .uri(URI.create(String.format("http://localhost:%d/complex-products", elepy.http().port())))
//                .build(), HttpResponse.BodyHandlers
//                .ofString()).body();
//
//
//        assertThat(adapter.getAmountOfProductChanges())
//                .isEqualTo(begin + 1);

    }


}