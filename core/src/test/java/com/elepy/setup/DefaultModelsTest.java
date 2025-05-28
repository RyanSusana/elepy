package com.elepy.setup;

import com.elepy.Elepy;
import com.elepy.MockHttpService;
import com.elepy.MockCrudFactory;
import com.elepy.auth.users.User;
import com.elepy.schemas.FieldType;
import com.elepy.schemas.Property;
import com.elepy.setup.validmodels.UserExtended;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DefaultModelsTest {
    private Elepy sut;

    @BeforeEach
    void setup() {
        sut = new Elepy()
                .withDefaultCrudFactory(MockCrudFactory.class)
                .withHttpService(MockHttpService.class);

    }

    @Test
    void can_Override_DefaultModels() {
        sut.addModelPackage("com.elepy.setup.validmodels");

        sut.start();

        var testSubject1 = sut.modelSchemaFor(UserExtended.class);
        var testSubject2 = sut.modelSchemaFor(User.class);


        assertThat(testSubject1.getJavaClass()).isEqualTo(UserExtended.class);
        assertThat(testSubject2.getJavaClass()).isEqualTo(UserExtended.class);

        assertThat(sut.modelSchemaFor(UserExtended.class))
                .isNotNull()
                .extracting(input -> input.getProperty("extendedUserProperty"))
                .isNotNull()
                .extracting(Property::getType)
                .isEqualTo(FieldType.TEXTAREA);

    }
}
