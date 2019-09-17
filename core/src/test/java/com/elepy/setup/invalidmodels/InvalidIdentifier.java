package com.elepy.setup.invalidmodels;

import com.elepy.annotations.Identifier;
import com.elepy.annotations.RestModel;

import java.util.Date;


@RestModel(name = "", slug = "/-")
public class InvalidIdentifier {

    @Identifier
    private Date id;
} 
