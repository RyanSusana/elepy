package com.elepy.di.circular;

import jakarta.inject.Inject;

public class Circular1 {
    @Inject
    Circular3 circular3;
}
