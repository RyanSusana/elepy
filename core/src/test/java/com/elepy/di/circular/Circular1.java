package com.elepy.di.circular;

import com.elepy.annotations.Inject;

public class Circular1 {
    @Inject
    Circular3 circular3;
}
