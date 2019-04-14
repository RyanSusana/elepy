package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.exceptions.ElepyException;
import com.elepy.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

class UnsatisfiedDependencies {
    private static final Logger logger = LoggerFactory.getLogger(UnsatisfiedDependencies.class);
    private final DefaultElepyContext elepyContext;
    private final Set<Class> scannedClasses = new HashSet<>();
    private Set<ContextKey> unsatisfiedKeys = new HashSet<>();
    private Set<ContextKey> allDependencies = new HashSet<>();


    UnsatisfiedDependencies(DefaultElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }

    private static ContextKey toKey(AnnotatedElement field) {

        Inject annotation = field.getAnnotation(Inject.class);

        final Class<?> type;

        if (annotation == null || annotation.type().equals(Object.class)) {
            if (field instanceof Field) {
                type = ((Field) field).getType();
            } else if (field instanceof Parameter) {
                type = ((Parameter) field).getType();
            } else {
                throw new ElepyException("This should never be thrown: Annotated element is not a Field or Parameter", 500);
            }
        } else {
            type = annotation.type();
        }
        return new ContextKey<>(type, ElepyContext.getTag(field));

    }

    private String errorString(Set<ContextKey> keys) {
        StringBuilder sb = new StringBuilder();

        for (ContextKey key : keys) {
            sb.append("[").append(key.getType().getSimpleName());


            if (!key.getTag().isEmpty()) {
                sb.append(": \"").append(key.getTag()).append("\"");
            }
            sb.append("], ");
        }
        return sb.toString();
    }

    private void getAllInnerUnsatisfiedDependencies(Class<?> root) {

        Optional<Constructor<?>> elepyAnnotatedConstructor =

                ElepyContext.getElepyAnnotatedConstructor(root);

        if (elepyAnnotatedConstructor.isPresent()) {
            for (Parameter constructorParam : elepyAnnotatedConstructor.get().getParameters()) {
                addAnnotatedElementDependency(constructorParam);
            }
        }


        for (Field field : ReflectionUtils.searchForFieldsWithAnnotation(root, Inject.class)) {
            addAnnotatedElementDependency(field);

        }
    }

    private void addAnnotatedElementDependency(AnnotatedElement element) {
        ContextKey contextKey = toKey(element);
        unsatisfiedKeys.add(contextKey);

        if (!scannedClasses.contains(contextKey.getType())) {
            scannedClasses.add(contextKey.getType());

            getAllInnerUnsatisfiedDependencies(contextKey.getType());
        }
    }

    public void tryToSatisfy() {
        for (ContextKey unsatisfiedKey : new ArrayList<>(unsatisfiedKeys)) {
            getAllInnerUnsatisfiedDependencies(unsatisfiedKey.getType());
        }
        allDependencies.addAll(unsatisfiedKeys);
        tryToSatisfy(false);
    }

    public void tryToSatisfy(boolean alreadyTried) {


        if (alreadyTried) {
            allDependencies.removeAll(elepyContext.getDependencyKeys());
            throw new ElepyDependencyInjectionException(String.format("%n%nUnsatisfied or Circular Dependencies in: %s%nCurrent Dependencies: %s%nMissing Dependencies: %s%n", errorString(unsatisfiedKeys), errorString(elepyContext.getDependencyKeys()), errorString(allDependencies)), unsatisfiedKeys.size());
        }

        alreadyTried = true;
        List<ContextKey> toRemove = new ArrayList<>();
        Map<ContextKey, Object> toAdd = new HashMap<>();
        for (ContextKey contextKey : unsatisfiedKeys) {
            try {
                Object o = elepyContext.initializeElepyObject(contextKey.getType());
                ContextKey objectContextKey = new ContextKey<>(contextKey.getType(), ElepyContext.getTag(contextKey.getType()));
                elepyContext.registerDependency(objectContextKey.getType(), objectContextKey.getTag(), o);


                toAdd.put(objectContextKey, o);
            } catch (ElepyErrorMessage e) {

                logger.debug("Elepy error msg", e);
            } catch (Exception e) {

                logger.error("Dependency injection error", e);
            }
            if (elepyContext.getDependencyKeys().contains(contextKey)) {
                alreadyTried = false;
                toRemove.add(contextKey);
            }
        }
        unsatisfiedKeys.removeAll(toRemove);

        if (!unsatisfiedKeys.isEmpty()) {
            tryToSatisfy(alreadyTried);
        }

    }


    public void add(ContextKey key) {
        unsatisfiedKeys.add(key);
    }

}
