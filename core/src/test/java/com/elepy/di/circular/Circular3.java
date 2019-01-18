package com.elepy.di.circular;

import com.elepy.annotations.Inject;

public class Circular3 {
    @Inject
    Circular1 circular1;

    @Inject
    Circular2 circular2;
}
