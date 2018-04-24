package com.ryansusana.elepy.annotations;

import java.util.Comparator;

public @interface RestComparator {
    Class<? extends Comparator> using();
}
