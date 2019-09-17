package com.elepy.setup.complexmodel;

import com.elepy.annotations.Find;
import com.elepy.annotations.RestModel;

@RestModel(
        name = "ComplexProduct",
        slug = "/complex-products"
)
@Find(findManyHandler = ComplexProductUpdate.class)
public class ComplexProduct {

    private String id;
} 
