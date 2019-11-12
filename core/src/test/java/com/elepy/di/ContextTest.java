package com.elepy.di;

import com.elepy.Base;
import com.elepy.di.circular.Circular1;
import com.elepy.di.circular.Circular2;
import com.elepy.di.circular.Circular3;
import com.elepy.di.named.Named1;
import com.elepy.di.named.Named2;
import com.elepy.di.named.Unnamed1;
import com.elepy.di.threeway.Dependency1;
import com.elepy.di.threeway.Dependency2;
import com.elepy.di.threeway.Dependency3;
import com.elepy.di.threeway.Unsatisfiable;
import com.elepy.di.tree.*;
import com.elepy.di.unsatisfiedconstructor.DelegationAssistant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ContextTest extends Base {

    @Test
    void testDISuccessful() {

        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();


        defaultElepyContext.registerDependency(Dependency2.class);
        defaultElepyContext.registerDependency(Dependency3.class);
        defaultElepyContext.registerDependency(Dependency1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
    }

    @Test
    void testCircularDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Circular3.class);
        defaultElepyContext.registerDependency(Circular2.class);
        defaultElepyContext.registerDependency(Circular1.class);

        defaultElepyContext.strictMode(true);

        assertThatExceptionOfType(ElepyDependencyInjectionException.class)
                .isThrownBy(defaultElepyContext::resolveDependencies);

    }

    @Test
    void testUnsatisfiedDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Dependency3.class);
        defaultElepyContext.registerDependency(Dependency1.class);
        defaultElepyContext.registerDependency(Unsatisfiable.class);

        defaultElepyContext.strictMode(true);
        assertThatExceptionOfType(ElepyDependencyInjectionException.class)
                .isThrownBy(defaultElepyContext::resolveDependencies);

    }

    @Test
    void testCorrectStrictMode() {

        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);

        assertDoesNotThrow(() -> {
            defaultElepyContext.registerDependency(Dependency1.class);
            defaultElepyContext.registerDependency(Dependency2.class);
            defaultElepyContext.registerDependency(Dependency3.class);
        });
    }

    @Test
    void testIncorrectStrictMode() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();
        defaultElepyContext.strictMode(true);

        assertThatExceptionOfType(ElepyDependencyInjectionException.class)
                .isThrownBy(() -> defaultElepyContext.registerDependency(Unsatisfiable.class));

    }

    @Test
    void testTaggedDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Named1.class);
        defaultElepyContext.registerDependency(Named2.class);
        defaultElepyContext.registerDependency(Unnamed1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
    }

    @Test
    void testTreeDependenciesPreFilled() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Node1.class);
        defaultElepyContext.registerDependency(Node2.class);
        defaultElepyContext.registerDependency(Node3.class);
        defaultElepyContext.registerDependency(Node4.class);
        defaultElepyContext.registerDependency(Node5.class);
        defaultElepyContext.registerDependency(Node6.class);
        defaultElepyContext.registerDependency(Node7.class);
        defaultElepyContext.registerDependency(Node8.class);
        defaultElepyContext.registerDependency(Node9.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
        assertThat(defaultElepyContext.getDependencyKeys().size()).isEqualTo(9);
    }

    @Test
    void testTreeDependenciesBuild() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(Node1.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);
        assertThat(defaultElepyContext.getDependencyKeys().size()).isEqualTo(9);
    }

    @Test
    void testUnsatisfiedConstructorDependencies() {
        DefaultElepyContext defaultElepyContext = new DefaultElepyContext();

        defaultElepyContext.registerDependency(DelegationAssistant.class);

        assertDoesNotThrow(defaultElepyContext::resolveDependencies);

    }
}
