package com.elepy.di;

import com.elepy.annotations.Inject;
import com.elepy.exceptions.ElepyErrorMessage;
import com.elepy.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class UnsatisfiedDependencies {
    private final DefaultElepyContext elepyContext;
    private final Set<Class> scannedClasses = new HashSet<>();
    private Set<ContextKey> unsatisfiedKeys = new HashSet<>();
    private Set<ContextKey> allDependencies = new HashSet<>();

    UnsatisfiedDependencies(DefaultElepyContext elepyContext) {
        this.elepyContext = elepyContext;
    }

    private static ContextKey toKey(Field field) {

        Inject annotation = field.getAnnotation(Inject.class);
        Class<?> type = annotation.classType().equals(Object.class) ? field.getType() : annotation.classType();

        return new ContextKey<>(type, ElepyContext.getTag(field));

    }

    private String errorString(Set<ContextKey> keys) {
        StringBuilder sb = new StringBuilder();

        for (ContextKey key : keys) {
            sb.append("[").append(key.getClassType().getSimpleName());


            if (!key.getTag().isEmpty()) {
                sb.append(": \"").append(key.getTag()).append("\"");
            }
            sb.append("], ");
        }
        return sb.toString();
    }

    private void subs(Class<?> root) {
        for (Field field : ClassUtils.searchForFieldsWithAnnotation(root, Inject.class)) {
            ContextKey contextKey = toKey(field);
            unsatisfiedKeys.add(contextKey);

            if (!scannedClasses.contains(contextKey.getClassType())) {
                scannedClasses.add(contextKey.getClassType());

                subs(contextKey.getClassType());
            }

        }
    }

    public void tryToSatisfy() {
        for (ContextKey unsatisfiedKey : new ArrayList<>(unsatisfiedKeys)) {
            subs(unsatisfiedKey.getClassType());
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
        for (ContextKey contextKey : unsatisfiedKeys) {
            try {
                Object o = elepyContext.initializeElepyObject(contextKey.getClassType());
                ContextKey objectContextKey = new ContextKey<>(contextKey.getClassType(), ElepyContext.getTag(contextKey.getClassType()));
                elepyContext.registerDependency(objectContextKey.getClassType(), objectContextKey.getTag(), o);
            } catch (Exception e) {
                if (!(e instanceof ElepyErrorMessage)) {
                    e.printStackTrace();
                }
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
