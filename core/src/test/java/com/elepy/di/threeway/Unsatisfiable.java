package com.elepy.di.threeway;

import com.elepy.annotations.Inject;

public class Unsatisfiable {
    @Inject(tag = "no way")
    Dependency1 dependency1;
}
