package com.elepy.di.threeway;

import com.elepy.dao.Crud;
import jakarta.inject.Inject;

public class Unsatisfiable {
    //This is unsatisfiable
    @Inject
    Crud<Dependency1> dependency1;
}
