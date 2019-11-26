package com.elepy;

import com.elepy.auth.Token;
import com.elepy.auth.User;
import com.elepy.uploads.FileReference;

import java.util.Set;

public class Defaults {

    static final String HTTP_SERVICE = "com.elepy.sparkjava.SparkService";

    static final Set<Class<?>> MODELS = Set.of(User.class, FileReference.class, Token.class);

    private Defaults() {
    }
} 
