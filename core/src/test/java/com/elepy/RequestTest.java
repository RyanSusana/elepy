package com.elepy;

import com.elepy.http.Request;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled("Replace with better mocking of Request and Response")
public class RequestTest {

    @Spy
    private Request request;


    @Test
    void inputAs_ReturnsCorrectObject() {
        when(request.body()).thenReturn("{\"id\": \"fake-id\"}");

        assertThat(request.inputAs(Product.class).getId())
                .isEqualTo("fake-id");

        assertThat(request.inputAsString())
                .as("single  field inputs should be returnable as String")
                .isEqualTo("fake-id");
    }


    @Test
    void token_ReturnsCorrectToken() {
        when(request.headers("Authorization")).thenReturn("Bearer fake-token");

        assertThat(request.token())
                .isEqualTo("fake-token");

    }
}
