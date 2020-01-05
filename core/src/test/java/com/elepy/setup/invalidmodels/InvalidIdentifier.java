package com.elepy.setup.invalidmodels;

import com.elepy.annotations.Identifier;
import com.elepy.annotations.Model;

import java.util.Date;


@Model(name = "", path = "/-")
public class InvalidIdentifier {

    @Identifier
    private Date id;
} 
