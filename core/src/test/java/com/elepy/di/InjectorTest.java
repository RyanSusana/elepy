package com.elepy.di;

import com.elepy.dao.Crud;
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
}
