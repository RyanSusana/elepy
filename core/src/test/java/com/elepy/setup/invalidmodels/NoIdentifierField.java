package com.elepy.setup.invalidmodels;

import com.elepy.annotations.RestModel;

@RestModel(name = "NoIdentifierField", path = "/-")
public class NoIdentifierField {
    private String someArbitraryField;
} 
