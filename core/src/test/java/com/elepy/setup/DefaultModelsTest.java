package com.elepy.setup;

import com.elepy.Elepy;
import com.elepy.MockCrudFactory;
import com.elepy.auth.User;
import com.elepy.exceptions.ElepyException;
import com.elepy.http.HttpService;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.setup.validmodels.UserExtended;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

public class DefaultModelsTest {
    private Elepy sut;

    @BeforeEach
    void setup() {
        sut = new Elepy()
                .withDefaultCrudFactory(MockCrudFactory.class)
                .withHttpService(mock(HttpService.class));

    }

    @Test
    void can_Override_DefaultModels() {
        sut.addModelPackage("com.elepy.setup.validmodels");

        sut.start();

        assertThat(sut.modelSchemaFor(UserExtended.class))
                .isNotNull()
                .extracting(input -> input.getProperty("extendedUserProperty"))
                .isNotNull()
                .extracting(Property::getType)
                .isEqualTo(FieldType.TEXTAREA);


        assertThatExceptionOfType(ElepyException.class)
                .isThrownBy(() -> sut.modelSchemaFor(User.class));

    }
}
