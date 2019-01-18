package com.elepy.di.named;

import com.elepy.annotations.Inject;

public class Unnamed1 {

    @Inject(tag = "named1")
    private Named1 named1;

    @Inject(tag = "named2")
    private Named2 named2;
}
