package com.elepy.di;

import jakarta.inject.Inject;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Resolver {
    private static final Logger logger = LoggerFactory.getLogger(Resolver.class);


    private final Set<ContextKey> unsatisfiedDependencies ;
    private final Set<ContextKey> satisfiedDependencies ;


    Resolver() {
        this.unsatisfiedDependencies = new HashSet<>();
        this.satisfiedDependencies = new HashSet<>();
    }

    void addUnsatisfiedDependency(ContextKey contextKey) {
        unsatisfiedDependencies.add(contextKey);
    }

    void resolve(DefaultElepyContext context) {
        for (ContextKey unsatisfiedKey : new ArrayList<>(unsatisfiedDependencies)) {
            findDependencies(unsatisfiedKey.getType())
                    .forEach(this::addUnsatisfiedDependency);
        }
        satisfiedDependencies.addAll(unsatisfiedDependencies);
        resolve(false, context);
    }


    private Stream<ContextKey<?>> findDependencies(Class<?> root) {
        return Stream.concat(
                findDependenciesInConstructor(root),
                findDependenciesInFields(root)
        );

    }

    private Stream<? extends ContextKey<?>> findDependenciesInFields(Class<?> root) {
        return ReflectionUtils.searchForFieldsWithAnnotation(root, Inject.class)
                .stream()
                .map(ContextKey::forAnnotatedElement);
    }

    private Stream<? extends ContextKey<?>> findDependenciesInConstructor(Class<?> root) {
        return ReflectionUtils.getElepyConstructor(root)
                .map(constructor ->
                        Arrays.stream(constructor.getParameters())
                                .map(ContextKey::forAnnotatedElement))
                .orElse(Stream.empty());
    }

    private void resolve(boolean alreadyTried, DefaultElepyContext elepyContext) {
        if (alreadyTried) {
            satisfiedDependencies.removeAll(elepyContext.getDependencyKeys());
            throw new ElepyDependencyInjectionException(String.format("%n%nUnsatisfied or Circular Dependencies in: %s%nCurrent Dependencies: %s%nMissing Dependencies: %s%n", keysToString(unsatisfiedDependencies), keysToString(elepyContext.getDependencyKeys()), keysToString(satisfiedDependencies)), unsatisfiedDependencies.size());
        }

        alreadyTried = true;
        List<ContextKey> toRemove = new ArrayList<>();
        for (ContextKey contextKey : unsatisfiedDependencies) {
            try {
                Object o = elepyContext.initialize(contextKey.getType());
                ContextKey objectContextKey = new ContextKey<>(contextKey.getType(), null);
                elepyContext.registerDependency(objectContextKey.getType(), objectContextKey.getTag(), o);
            } catch (ElepyException e) {

                logger.error("Dependency injection error", e);
            }
            if (elepyContext.getDependencyKeys().contains(contextKey)) {
                alreadyTried = false;
                toRemove.add(contextKey);
            }
        }
        unsatisfiedDependencies.removeAll(toRemove);

        if (!unsatisfiedDependencies.isEmpty()) {
            resolve(alreadyTried, elepyContext);
        }

    }

    private String keysToString(Set<ContextKey> keys) {
        return keys.stream()
                .map(this::keyToString)
                .collect(Collectors.joining(","));

    }

    private String keyToString(ContextKey key) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(key.getType().getSimpleName());

        if (!key.getTag().isEmpty()) {
            sb.append(": \"").append(key.getTag()).append("\"");
        }

        sb.append("]");

        return sb.toString();
    }
}
