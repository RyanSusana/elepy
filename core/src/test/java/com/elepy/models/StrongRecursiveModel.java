package com.elepy.models;

import com.elepy.annotations.InnerObject;

public class StrongRecursiveModel {
    private String id;


    @InnerObject(maxRecursionDepth = 8)
    private DirectRecursiveObject recursiveObject;


}
