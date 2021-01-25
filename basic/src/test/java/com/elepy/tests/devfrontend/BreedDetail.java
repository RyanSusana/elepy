package com.elepy.tests.devfrontend;

import com.elepy.annotations.ShowIf;

import javax.annotation.RegEx;
import javax.validation.constraints.Pattern;

@ValidDogBreed
public class BreedDetail {
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;

    @Pattern(regexp = "[0-9]+")
    private String size;

    @ShowIf("root.name == 'doggo'")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
