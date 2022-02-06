package com.elepy.di.circular;

import jakarta.inject.Inject;

public class Circular3 {
    @Inject
    Circular1 circular1;

    @Inject
    Circular2 circular2;
}
