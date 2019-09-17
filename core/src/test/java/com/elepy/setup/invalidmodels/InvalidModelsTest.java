package com.elepy.setup.invalidmodels;

import com.elepy.Base;
import com.elepy.Elepy;
import com.elepy.exceptions.ElepyConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvalidModelsTest extends Base {

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
    void testNoIdentifier() {
        final var elepyConfigException =
                assertThrows(ElepyConfigException.class,
                        () -> elepy.addModel(NoIdentifierField.class)
                                .start());


        assertThat(elepyConfigException.getMessage())
                .contains("@Identifier");
    }

    @Test
    void testInvalidIdentifier() {
        final var elepyConfigException =
                assertThrows(ElepyConfigException.class,
                        () -> elepy.addModel(InvalidIdentifier.class)
                                .start());

        assertThat(elepyConfigException.getMessage())
                .containsMatch("Long|String|Int");
    }

    @Test
    void testNoRestModelAnnotation() {
        final var elepyConfigException =
                assertThrows(ElepyConfigException.class,
                        () -> elepy.addModel(NoRestModelAnnotation.class)
                                .start());

        assertThat(elepyConfigException.getMessage())
                .contains("@RestModel");
    }
}
