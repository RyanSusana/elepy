package com.elepy.tests.devfrontend;

import javax.annotation.RegEx;
import javax.validation.constraints.Pattern;

@ValidDogBreed
public class BreedDetail {
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;
    
    @Pattern(regexp = "[0-9]+")
    private String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
