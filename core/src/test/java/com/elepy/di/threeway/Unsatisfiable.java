package com.elepy.di.threeway;

import jakarta.inject.Inject;
import com.elepy.dao.Crud;

public class Unsatisfiable {
    //This is unsatisfiable
    @Inject
    Crud<Dependency1> dependency1;
}
