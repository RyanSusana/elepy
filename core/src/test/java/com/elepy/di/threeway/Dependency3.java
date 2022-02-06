package com.elepy.di.threeway;

import jakarta.inject.Inject;

public class Dependency3 {
    @Inject
    private Dependency1 dep1;

    @Inject
    private Dependency2 dep2;
}
