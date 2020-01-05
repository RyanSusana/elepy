package com.elepy.setup.invalidmodels;

import com.elepy.annotations.Model;

@Model(name = "NoIdentifierField", path = "/-")
public class NoIdentifierField {
    private String someArbitraryField;
} 
