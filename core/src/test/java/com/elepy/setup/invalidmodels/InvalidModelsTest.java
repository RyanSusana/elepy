package com.elepy.setup.invalidmodels;

import com.elepy.Base;
import com.elepy.Elepy;
import com.elepy.exceptions.ElepyConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

        assertThatExceptionOfType(ElepyConfigException.class).isThrownBy(() -> elepy.addModel(NoIdentifierField.class)
                .start())
                .withMessageContaining("@Identifier");

    }

    @Test
    void testInvalidIdentifier() {
        assertThatExceptionOfType(ElepyConfigException.class)
                .isThrownBy(() -> elepy.addModel(InvalidIdentifier.class).start());
    }

    @Test
    void testNoRestModelAnnotation() {
        assertThatExceptionOfType(ElepyConfigException.class)
                .isThrownBy(() -> elepy.addModel(NoRestModelAnnotation.class).start())
                .withMessageContaining("@Model");

    }
}
