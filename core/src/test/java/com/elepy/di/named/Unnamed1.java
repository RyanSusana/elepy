package com.elepy.di.named;

import jakarta.inject.Inject;

public class Unnamed1 {

    @Inject
    private Named1 named1;

    @Inject
    private Named2 named2;
}
