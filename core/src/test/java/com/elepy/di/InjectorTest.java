package com.elepy.di;

import com.elepy.crud.Crud;
import com.elepy.di.props.Props;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectorTest {

    private Injector sut;
    private DefaultElepyContext defaultElepyContext;

    @BeforeEach
    void setUp() {
        defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);
        sut = new Injector(defaultElepyContext);
    }

    @Test
    void testCrudInjection() {

        defaultElepyContext.strictMode(true);
        defaultElepyContext.registerDependencySupplier(Crud.class, "/resources", MockCrudResource::new);
        final var mockCrudService = sut.initializeAndInject(MockCrudService.class);

        assertThat(mockCrudService.getCrudGeneric())
                .isNotNull();

        assertThat(mockCrudService.getCrudResource())
                .isNotNull();
    }

    @Test
    void testPropertyInjection() {
        final var propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.addProperty("smtp.server", "ryan");
        propertiesConfiguration.addProperty("test", true);

        defaultElepyContext.registerDependency(Configuration.class, propertiesConfiguration);

        final var props = sut.initializeAndInject(Props.class);

        assertThat(props.getSmtpServer())
                .isEqualTo("ryan");

        assertThat(props.isTestBoolean())
                .isTrue();
        assertThat(props.getWithDefault())
                .isEqualTo("isAvailable");
    }
}
